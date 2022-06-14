- create a .gitlab-ci.yml at the root of the gitlab

- create an eval script

- create a docker file

- build the docker image
docker build -f Dockerfile -t ttoolci:latest .

- run the docker image
docker run -it --rm ttoolci:latest bash


- launch the gitlab-runner so as to start a docker machine for each pull

sudo gitlab-runner register -n --name ttool@yvrac -r G8WEts9yb9cebut2aTSz -u https://gitlab.telecom-paris.fr/ --executor docker --docker-image ttoolci:latest --docker-pull-policy never
