# Copyright 2024 Google LLC

# Get the directory for this file, and use that instead of a fixed path.
local_dir := $(dir $(lastword $(MAKEFILE_LIST)))

# FLAG_DECLARATION_FILES gives the path(s) of flag declaration files that
# should be included in the build.
FLAG_DECLARATION_FILES :=

# Attach the flag value definitions to the various release configurations.
$(call declare-release-config, trunk_staging, $(local_dir)build_config/fail.scl)

local_dir :=
