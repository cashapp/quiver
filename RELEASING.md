Releasing
=========

### Prerequisite: Sonatype (Maven Central) Account

Create an account on the [Sonatype issues site](https://issues.sonatype.org/). Ask an existing publisher to open
an issue requesting publishing permissions for `app.cash` projects. They can use [this issue](https://issues.sonatype.org/browse/OSSRH-84839)
as a template to copy.

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

3. Update `CHANGELOG.md` with changes since the last release. This
   step is manual and somewhat tedious. Follow the existing `CHANGELOG.md` format.

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
    # Then create PR and merge it
    ``` 

6. Trigger the "Publish a release" action manually. This will publish to
[Sonatype Nexus](https://oss.sonatype.org/), closing and releasing the artifact automatically to
promote it to Maven Central.  Note that it can take 10 to 30 minutes or more for the artifacts to
appear on Maven Central.

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
    # The create PR and merge it
    ```

9. Draft a new [release](https://docs.github.com/en/github/administering-a-repository/managing-releases-in-a-repository) of `A.B.C` and publish it.
    - (Trialing) Copy release notes added to `CHANGELOG.md` in step 1 into Github release.

## Troubleshooting

- If the github action fails, drop the artifacts from Sonatype and re run the job.
