Releasing
=========

### Steps

1. Set versions:

    ```sh
    export RELEASE_VERSION=A.B.C
    export NEXT_VERSION=A.B.D-SNAPSHOT
    ```

2. Check out the release branch.

    ```sh
    git checkout -b release-$RELEASE_VERSION
    ```

3. Update `CHANGELOG.md` with changes since the last release. Follow the existing `CHANGELOG.md` format, which is
   derived from [this guide](https://keepachangelog.com/en/1.0.0/)

4. Update documentation and Gradle properties with `RELEASE_VERSION`

    ```sh
    sed -i "" \
      "s/VERSION_NAME=.*/VERSION_NAME=$RELEASE_VERSION/g" \
      gradle.properties
    ```

5. Tag the release and push to GitHub. Submit and merge PR.

    ```sh
    git commit -am "Prepare for release $RELEASE_VERSION."
    git tag -a quiver-$RELEASE_VERSION -m "Version $RELEASE_VERSION"
    git push && git push --tags
    gh pr create -f && gh pr merge --auto --squash
    ``` 

6. Wait until the PR created above is merged, then trigger the
   [Publish a release](https://github.com/cashapp/quiver/actions/workflows/Release.yml) action against the new tag.
   This will publish to [Sonatype Nexus](https://oss.sonatype.org/), closing and releasing the artifact
   automatically to promote it to Maven Central. Note that it can take 10 to 30 minutes or more for the
   artifacts to appear on Maven Central.


7. Checkout `main` branch and pull the latest changes

    ```sh
    git checkout main
    git pull
    ```

8. In a new branch, prepare for the next release and push to GitHub. Submit and merge PR.

    ```sh
    git checkout -b next-version-$NEXT_VERSION
    sed -i "" \
      "s/VERSION_NAME=.*/VERSION_NAME=$NEXT_VERSION/g" \
      gradle.properties
    git commit -am "Prepare next development version."
    git push
    gh pr create -f && gh pr merge --auto --squash
    ```

9. [Draft a new release](https://github.com/cashapp/nostrino/releases/new) of `A.B.C` and publish it. Copy release
   notes added to `CHANGELOG.md` in step 1 into the release description.

## Troubleshooting

- If the GitHub action fails, drop the artifacts from Sonatype and re-run the job. A Sonatype account with app.cash permissions
    is required. Raise an issue if you do not have this.
