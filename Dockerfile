FROM ubuntu:22.04

ENV USERNAME=osgi 
ENV HOME_DIR=/osgi 
ENV OPT_DIR=/opt/osgi
ENV GECKO_VERSION=2.0.0

RUN mkdir -p ${OPT_DIR} && \
    mkdir -p ${OPT_DIR}/logs && \
    mkdir -p /tmp
    
#Install java 17
RUN apt update && apt upgrade -y
RUN apt-get install openjdk-17-jdk -y
RUN apt-get install openjdk-17-jre -y

# Install python3 and pip
RUN apt update && apt upgrade -y
RUN apt install python3 -y && apt install python3-pip -y

# Chcek python version
RUN python3 --version

# Install needed python deps
RUN pip install -U numpy 
RUN pip install -U nltk
RUN [ "python3", "-c", "import nltk; nltk.download('stopwords', download_dir='/usr/share/nltk_data')" ]
RUN [ "python3", "-c", "import nltk; nltk.download('punkt', download_dir='/usr/share/nltk_data')" ]
RUN [ "python3", "-c", "import nltk; nltk.download('wordnet', download_dir='/usr/share/nltk_data')" ]
RUN pip install -U stringutils && pip install -U python-string-utils
RUN pip install -U pandas
RUN pip install -U odfpy

# Install spacy and its pre-trained models with no vectors, md and lg vectors
RUN pip install -U pip setuptools wheel
RUN pip install -U spacy
RUN python3 -m spacy download en_core_web_sm
RUN python3 -m spacy download en_core_web_md
RUN python3 -m spacy download en_core_web_lg

# Install negspacy
RUN pip install negspacy

# Install sklearn
RUN pip install -U scikit-learn

# Copy jar into container
COPY de.avatar.mr.vaadin/generated/export/avatar-mr.jar ${OPT_DIR}/

#Copy data folder
COPY de.avatar.mr.vaadin/data/ ${OPT_DIR}/data/

# Create HOME_DIR and adjust permissions
RUN mkdir ${HOME_DIR}
RUN addgroup --system --gid 7743 ${USERNAME} 
RUN adduser --system --uid 7743 --home ${HOME_DIR} --shell /bin/false --ingroup ${USERNAME} ${USERNAME}
RUN chown -R ${USERNAME} ${OPT_DIR} && \
    chmod -R u+rwx ${OPT_DIR}
    
# Enter OPT_DIR
RUN cd ${OPT_DIR}

# Expose port 8086 which we would need for the UI
EXPOSE 8086

# Create some volumes to access content of container
VOLUME ${HOME_DIR}
VOLUME ${OPT_DIR}

WORKDIR ${OPT_DIR}
USER ${USERNAME}

CMD ["java", "-jar", "/opt/osgi/avatar-mr.jar"]
