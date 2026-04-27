# docxkt autonomous sandbox
#
# Non-root Claude Code sandbox with:
#   - Node 22 + dolanmiu/docx baked-in and built at /opt/docx-ref
#   - OpenJDK 21 for Gradle/Kotlin
#   - ripgrep, fd, jq, standard unix utilities
#
# Build:
#   docker buildx build \
#     --build-context docx-src=~/path/to/your/dolanmiu-docx-clone \
#     --build-arg DOCX_SRC_SHA=$(git -C ~/path/to/your/dolanmiu-docx-clone rev-parse HEAD) \
#     -t docxkt-sandbox .
#
# Run: see CONTEXT.md for the docxkt-yolo shell helper.

FROM node:22-trixie-slim

# System packages
RUN apt-get update && apt-get install -y --no-install-recommends \
        openjdk-21-jdk-headless \
        git curl ca-certificates \
        unzip zip \
        ripgrep fd-find jq \
        sudo less \
    && rm -rf /var/lib/apt/lists/*

# Claude Code (installed as root so it lives in the global npm prefix)
RUN npm install -g @anthropic-ai/claude-code

# Reuse the non-root `node` user (UID/GID 1000) that ships with the base image.
# Grant passwordless sudo for in-container ops (apt-get, etc.).
RUN echo "node ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/node \
    && chmod 0440 /etc/sudoers.d/node

# Prepare reference and fixture dirs, plus the two named-volume mount
# points used at runtime (~/.claude and ~/.gradle). All owned by `node`.
# Without pre-creating ~/.claude and ~/.gradle, Docker creates them on
# first `docker run -v` from root:root — the non-root `node` user then
# can't write to them and Claude Code hangs silently on config init.
RUN mkdir -p /opt/docx-ref /opt/fixtures /home/node/.claude /home/node/.gradle \
    && chown -R node:node /opt/docx-ref /opt/fixtures /home/node/.claude /home/node/.gradle

USER node

# Bake dolanmiu/docx into the image from a named build context.
# Pass the host clone path at build time:
#   docker buildx build --build-context docx-src=<abs-path-to-local-docx-clone> ...
# This keeps the reference version explicit (host git SHA) and avoids fetching
# from GitHub during image build.
COPY --chown=node:node --from=docx-src . /opt/docx-ref/

WORKDIR /opt/docx-ref
# --legacy-peer-deps: the repo's package-lock.json at the pinned SHA has a
# peer-dep conflict (vite 8 vs vite-plugin-node-polyfills wanting <= 7).
# Safe for our use case — we're consuming the built output, not publishing.
RUN npm ci --legacy-peer-deps && npm run build

# Label the image with the dolanmiu/docx commit SHA for traceability.
# Passed by the caller; falls back to "unknown" if omitted.
ARG DOCX_SRC_SHA=unknown
LABEL io.docxkt.docx-ref-sha="${DOCX_SRC_SHA}"

# Force IPv4 for outbound connections.
# Node 17+ defaults to `verbatim` DNS ordering, which prefers IPv6 when both
# records exist. On Docker Desktop (Mac) the container has no working IPv6
# route by default, so Claude CLI hangs on connect to IPv6-resolved hosts
# like api.anthropic.com. `--dns-result-order=ipv4first` makes Node prefer A
# records; `gai.conf` tweak covers curl / other tools.
ENV NODE_OPTIONS="--dns-result-order=ipv4first"
RUN sudo sh -c 'echo "precedence ::ffff:0:0/96  100" >> /etc/gai.conf'

WORKDIR /workspace
