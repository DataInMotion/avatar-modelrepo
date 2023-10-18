# Avatar Model Repo

This project provides a set of functionalities to work with EMF models, such as:

+ Indexing and search for `EPackage`;
+ Generating model documentation;
+ Evaluate models and their instances based on certain privacy and/or open data criteria.

## Launching the Application

A docker image has been created via the `Dockerfile` that can be found in the root folder of this project. 

The image is located at `devel.data-in-motion.biz:6000/de.dim/avatar-mr:latest`.

To launch the image enter the `de.avatar.mr.vaadin/docker-compose` folder and launch the `docker-compose.yml` there (`docker-compose up -d`).

The Vaadin UI should then be available under `localhost::8086/avatar-mr`.

Please, notice that the `docker-compose` file creates two volumes, one for the `avatar-mr` docker service and one for `mongo`. The volumes are named `avatar-mr-volume` and `mongo-avatar-volume` respectively. When you do not need the container anymore, it's good practice to remove also these volumes (`docker volume rm avatar-mr-volume mongo-avatar-volume`), to keep everything clean on your host.

## Links

* [Documentation](https://github.com/DataInMotion/avatar-modelrepo)
* [Source Code](https://github.com/de-jena/MDO) (clone with `scm:git:git@github.com:DataInMotion/avatar-modelrepo.git`)


## Developers

* **Juergen Albert** (jalbert) / [j.albert@data-in-motion.biz](mailto:j.albert@data-in-motion.biz) @ [Data In Motion](https://www.datainmotion.de) - *architect*, *developer*
* **Mark Hoffmann** (mhoffmann) / [m.hoffmann@data-in-motion.biz](mailto:m.hoffmann@data-in-motion.biz) @ [Data In Motion](https://www.datainmotion.de) - *developer*, *architect*

## Licenses

**Apache License 2.0**

## Copyright

Data In Motion Consuling GmbH - All rights reserved

---
Data In Motion Consuling GmbH - [info@data-in-motion.biz](mailto:info@data-in-motion.biz)
