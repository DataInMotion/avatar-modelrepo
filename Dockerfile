#FROM ubuntu:22.04
FROM debian:12-slim

ENV USERNAME=osgi 
ENV HOME_DIR=/osgi 
ENV OPT_DIR=/opt/osgi
ENV GECKO_VERSION=2.0.0
ENV PYTHON_ENV=/python-env

RUN mkdir -p ${OPT_DIR} && \
    mkdir -p ${OPT_DIR}/logs && \
    mkdir -p /tmp
    
#Install java 17
RUN apt update && apt upgrade -y
RUN apt-get install openjdk-17-jdk -y
RUN apt-get install openjdk-17-jre -y

# Install python3 and pip
RUN apt-get update && apt-get upgrade -y
#RUN apt update && apt upgrade -y
RUN apt install python3 -y && apt install python3-pip -y

# Check python version
#RUN python3 --version

# This is important if we want to install spacy with GPU support (investigate!!)
#RUN apt -y install libcu++-dev 
#RUN /usr/local/cuda/bin/nvcc --version
#RUN /usr/local/cuda/bin/nvcc --version

#Create python virtual env
RUN apt install python3-venv -y
RUN python3 -m venv ${PYTHON_ENV}/env 
RUN . ${PYTHON_ENV}/env/bin/activate && which python3

# Copy python requirements 
COPY requirements.txt .
COPY ntlk-download.py .
COPY ntlk-download.sh .

# Install needed python deps
# The wheel package is needed before the other requirements, so we install it separately
RUN . ${PYTHON_ENV}/env/bin/activate && pip3 install -U wheel
RUN . ${PYTHON_ENV}/env/bin/activate && pip3 install -U -r requirements.txt

#Download some additional spacy data
RUN chmod 777 ./ntlk-download.sh
RUN ["/bin/bash", "-c", "./ntlk-download.sh"]

#Download spacy model with lg vectors
#RUN . ${PYTHON_ENV}/env/bin/activate && python3 -m spacy download en_core_web_lg

# Copy jar into container
COPY de.avatar.mr.vaadin/generated/export/avatar-mr.jar ${OPT_DIR}/

#Copy data folder
COPY de.avatar.mr.vaadin/data/ ${OPT_DIR}/data/

#Copy exec script to run application
COPY exec.sh ${OPT_DIR}/
RUN chmod 777 ${OPT_DIR}/exec.sh

# Create HOME_DIR and adjust permissions
RUN mkdir ${HOME_DIR}
RUN addgroup --system --gid 7743 ${USERNAME} 
RUN adduser --system --uid 7743 --home ${HOME_DIR} --shell /bin/false --ingroup ${USERNAME} ${USERNAME}
RUN chown -R ${USERNAME} ${OPT_DIR} && chmod -R u+rwx ${OPT_DIR}
    
# Enter OPT_DIR
RUN cd ${OPT_DIR}

# Expose port 8086 which we would need for the UI
EXPOSE 8086

# Create some volumes to access content of container
#VOLUME ${HOME_DIR}
#VOLUME ${OPT_DIR}

WORKDIR ${OPT_DIR}
USER ${USERNAME}

CMD ["/bin/bash", "-c", "./exec.sh"]
