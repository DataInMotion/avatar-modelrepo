# Avatar Model Repo

This project provides a set of functionalities to work with EMF models, such as:

+ Indexing and search for `EPackage`;
+ Generating model documentation;
+ Evaluate models and their instances based on certain privacy and/or open data criteria.

## Evaluate Models and their Instances

As part of the Vaadin UI available within this project, the user can evaluate dynamically loaded EMF meta models, as well as creating model instances and evaluate them, based on GDPR criteria of data privacy.

In order for the suggesters to work properly the corresponding models have to be downloaded and placed within the `de.avatar.mr.vaadin` bundle, under the `data` folder. Models are released in a .zip file previous granted permission. 

Once the models have been downloaded, unzipped and copied in the aforementioned folder the `launch.bndrun` in the same bundle can be executed and the Vaadin UI is available under `localhost::8086/avatar-mr`.

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
