#!/usr/bin/env bash
set -e

cd "$WORKING_DIR"/tools/certs || {
  echo "Error moving to the TLS-Certs directory."
  exit 1
}

echo ""
read -r -p 'Enter the <Domain Name> for your CA (Intermediate) certificate: ' ca_domain_name
if [ -z "$ca_domain_name" ]; then
  echo "ERROR: You must enter a <Domain Name> for your CA certificate."
  exit 1
fi
read -r -p 'Enter the <Domain Name> for your CSR (Server) certificate: ' server_domain_name
if [ -z "$server_domain_name" ]; then
  echo "ERROR: You must enter a <Domain Name> for your CSR certificate."
  exit 1
fi

### CREATE CA CERTIFICATE
echo ""
echo "Creating CA certificate..."
openssl ecparam                     \
  -name prime256v1                  \
  -genkey                           \
  -out ca-key.pem                   \
  -outform PEM

openssl req -new -x509 -sha256      \
  -key ca-key.pem                   \
  -out ca-cert.pem                  \
  -days 365                         \
  -subj "/C=EC/ST=Pichincha/L=UIO/O=Hiperium Company/OU=Innovation/CN=$ca_domain_name/emailAddress=support@$ca_domain_name"
echo "DONE!"

### CREATE CSR CERTIFICATE
echo ""
echo "Creating CSR certificate..."
openssl ecparam                 \
  -name prime256v1              \
  -genkey                       \
  -out server-key.pem           \
  -outform PEM

openssl req -new -sha256        \
  -key server-key.pem           \
  -out server-cert.pem          \
  -subj "/C=EC/ST=Pichincha/L=UIO/O=Hiperium Cloud/OU=Engineering/CN=$server_domain_name/emailAddress=support@$server_domain_name"
echo "DONE!"

echo ""
echo "Signing CSR certificate using CA..."
echo "subjectAltName = DNS:$AWS_WORKLOADS_ENV.$server_domain_name" > v3.ext
openssl x509 -req -sha256       \
  -in      server-cert.pem      \
  -CA      ca-cert.pem          \
  -CAkey   ca-key.pem           \
  -days    365                  \
  -extfile v3.ext               \
  -out     server-cert-"$AWS_WORKLOADS_ENV".pem   \
  -CAcreateserial
echo "DONE!"

### CREATE NEW CSR CERTIFICATE WITHOUT HEADER FROM CSR PRIVATE KEY
echo ""
echo "Creating new CSR without header from the original CSR Private Key..."
openssl ec -in server-key.pem -outform PEM -out server-key-no-header.pem
echo "DONE!"

### CREATING WORKLOAD ENVIRONMENT DIRECTORY
mkdir -p "$WORKING_DIR"/tools/certs/"$AWS_WORKLOADS_ENV"

echo ""
echo "Moving certificates to the '$AWS_WORKLOADS_ENV' Certification's directory..."
cp server-key-no-header.pem "$WORKING_DIR"/tools/certs/"$AWS_WORKLOADS_ENV"/server-key.pem
mv server-cert-"$AWS_WORKLOADS_ENV".pem "$WORKING_DIR"/tools/certs/"$AWS_WORKLOADS_ENV"
