The project allows to test different process instance versions running after the sample acme application upgrade.

# Prerequisites
- Containerization software e.g. [Docker desktop](https://www.docker.com/products/docker-desktop/)
- bash shell

# How to run
```shell
./run_test.sh
```
# How does it work
https://github.com/crystal-processes/crp-sample-upgrade-test/blob/19525f45634d414b39285a78e1fbaae4c1c457c7/run_test.sh#L1-L13

Generate data for release 0.1.0:
https://github.com/crystal-processes/crp-sample-upgrade-test/blob/689fa62f31561b3e011680add6cd2da72d9b4138/release-0.1.0/src/test/java/org/crp/flowable/springboot/sample/upgrade/GenerateDataForVersion1Test.java#L10-L22

The tests on the process instance from version 0.1.0 performed on the version 0.2.0.
https://github.com/crystal-processes/crp-sample-upgrade-test/blob/689fa62f31561b3e011680add6cd2da72d9b4138/release-0.2.0/src/test/java/org/crp/flowable/springboot/sample/upgrade/TestHelloWorldFromV1.java#L23-L41