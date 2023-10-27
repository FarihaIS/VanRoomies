#!/bin/sh

# Generate a self-signed certificate for a given IP address
# Credit to https://medium.com/@antelle/how-to-generate-a-self-signed-ssl-certificate-for-an-ip-address-f0dd8dddf754

# Usage example:
# sh generate-ip-cert.sh 127.0.0.1

IP=$1
if [ -z "$IP" ]; then
	IP="127.0.0.1"
fi
DIR=$(dirname $(readlink -f $0))
mkdir -p $DIR/../backend/certs
cd $DIR/../backend/certs

echo "Generating certificate for $IP"

curl -sS https://raw.githubusercontent.com/antelle/generate-ip-cert/master/generate-ip-cert.sh | bash -s $IP

cd -
