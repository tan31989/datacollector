#!/bin/bash
#
# Copyright 2021 StreamSets Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

if [[ ! -x "$(which gcloud)" ]]; then
  echo "GCloud CLI is needed to run GCE Deployment Bootstrap"
  exit -1
fi

http_count=0
for http_count in {0..2}; do
  http_code=$(curl --silent --output /dev/null --write-out "%{http_code}" http://metadata.google.internal/computeMetadata/v1/instance/attributes --connect-timeout 10 -H "Metadata-Flavor: Google" -L)
  if [ "200" = "${http_code}" ]; then
    break
  else
    sleep $(expr 2 + $http_count \* 2)
  fi
done

if [ "200" != "${http_code}" ]; then
  echo "Could not reach GCloud Metadata Service"
  exit -1
fi

do_source() {
  export STREAMSETS_DEPLOYMENT_SCH_URL="$(curl --silent http://metadata.google.internal/computeMetadata/v1/instance/attributes/schUrl --connect-timeout 10 -H "Metadata-Flavor: Google" -L)"
  export STREAMSETS_DEPLOYMENT_ID="$(curl --silent http://metadata.google.internal/computeMetadata/v1/instance/attributes/deploymentId --connect-timeout 10 -H "Metadata-Flavor: Google" -L)"
  tokenSecretId="$(curl --silent http://metadata.google.internal/computeMetadata/v1/instance/attributes/deploymentTokenSecretId --connect-timeout 10 -H "Metadata-Flavor: Google" -L)"
  export STREAMSETS_DEPLOYMENT_TOKEN=$(gcloud secrets versions access latest --secret=$tokenSecretId)
}
do_source
