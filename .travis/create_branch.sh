#!/bin/bash

echo "TRAVIS_BRANCH==>$TRAVIS_BRANCH"
echo "TRAVIS_REPO_SLUG=${TRAVIS_REPO_SLUG}"
if [ "$TRAVIS_BRANCH" == "master" ]; then

	git config --global user.email "travis@travis-ci.org"
	git config --global user.name "Travis CI"
	git remote add github https://${GH_TOKEN}@github.com/${TRAVIS_REPO_SLUG}.git > /dev/null 2>&1

	git branch -d "${VERSION}-gen"
	git push github ":${VERSION}-gen"
	git checkout -b "${VERSION}-gen"
	rm model/.docker.profile
	cp -R model/target/generated-sources/jooq/* model/src/main/java
	rm -rf model/target/generated-sources/jooq
	git add .
	git commit -m "Generate branch ${VERSION}-gen"

	git push origin "${VERSION}-gen"

fi
