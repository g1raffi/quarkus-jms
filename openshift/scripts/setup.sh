#!/bin/bash

echo "You are currently on project:"
echo "-------------"
oc status | head -1
echo "-------------"

echo "Are you sure you want to apply all resources for environment '$1'?"
echo "Press <Enter> to do so. Or press <Ctrl>+<C> to abort"
read DUMMY

ENVIRONMENT=$1
APPS="generic postgres postgres-ftp billing cockpit frontend periphery ftp it-mock bal-mock waf db-backup db-ftp-backup service-monitor-postgresql service-monitor-spring-boot"

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}" )" && pwd)"

if [ -z "$ENVIRONMENT" ]; then
  echo 'Usage: ./setup.sh ENVIRONMENT'
  exit 1
fi

if [ ! -z "$2" ]; then
  APPS=$2
fi


## configuration script for the environment ##

# add header (template start)
TEMPLATES="${DIR}/../template/_header.yml"

TEMPLATES="${TEMPLATES} ${DIR}/../template/apps/*.yml"

# add environment specific resources
if [ -d "${DIR}/../template/${ENVIRONMENT}" ]; then
  for resourceFile in `ls ${DIR}/../template/${ENVIRONMENT}`; do
    TEMPLATES="${TEMPLATES} ${DIR}/../template/${ENVIRONMENT}/${resourceFile}"
  done
fi

# add parameters (template end)
TEMPLATES="${TEMPLATES} ${DIR}/../template/_parameters.yml"

cat ${TEMPLATES} \
  | oc process -f - --param-file ${DIR}/../environments/${ENVIRONMENT}.yml \
  | oc apply -f -
