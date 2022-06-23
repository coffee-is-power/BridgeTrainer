FROM gitpod/workspace-base
RUN sudo apt update
RUN yes | sudo apt upgrade
RUN yes | sudo apt install openjdk-8-jdk openjdk-11-jdk npm
RUN sudo snpm install -g nodemon