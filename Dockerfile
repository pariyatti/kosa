FROM ubuntu:22.04

# Update the package manager
RUN apt-get update

# Install OpenJDK 17
RUN apt-get install -y openjdk-17-jdk

# Install wget and curl
RUN apt-get install -y wget curl

# Install Leiningen 2.9.1
RUN wget https://raw.githubusercontent.com/technomancy/leiningen/2.9.1/bin/lein \
    && mv lein /usr/local/bin/ \
    && chmod +x /usr/local/bin/lein \
    && lein

# Install Clojure
RUN wget https://download.clojure.org/install/linux-install-1.10.3.986.sh \
    && chmod +x linux-install-1.10.3.986.sh \
    && ./linux-install-1.10.3.986.sh

# Install Node.js LTS and npm
RUN apt-get install -y curl \
    && curl -sL https://deb.nodesource.com/setup_lts.x | bash - \
    && apt-get install -y nodejs \
    && npm install npm@latest -g

RUN npm install -g sass@1.51.0

# Create a directory for the application
RUN mkdir /app

# Copy project.clj into the Docker image to make it cachable
COPY project.clj /app/

# Set the working directory to /app
WORKDIR /app

# Install project dependencies using lein
RUN XTDB_ENABLE_BYTEUTILS_SHA1=true lein deps

# Copy the current directory into the container
COPY . /app

RUN npm install @webcomponents/custom-elements@1.0.0 --save \
    && cp node_modules/@webcomponents/custom-elements/custom-elements.min.js resources/public/js/custom-elements.min.js \
    && npm install @clr/icons@4.0.3 --save \
    && cp node_modules/@clr/icons/clr-icons.min.css resources/public/css/clr-icons.min.css \
    && cp node_modules/@clr/icons/clr-icons.min.js resources/public/js/clr-icons.min.js \
    && rm -rf node_modules

RUN mkdir -p ~/.kosa tmp/storage resources/storage

RUN XTDB_ENABLE_BYTEUTILS_SHA1=true lein scss :development once \
	&& XTDB_ENABLE_BYTEUTILS_SHA1=true lein cljsbuild once \
    && XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-migrate dev \
    && XTDB_ENABLE_BYTEUTILS_SHA1=true lein db-seed dev

CMD ["sh", "-c", "XTDB_ENABLE_BYTEUTILS_SHA1=true lein run -- -sf config/config.dev.edn"]
