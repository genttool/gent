GENT
====

GENT is a project template generator, inspired by conscript and g8, written in Groovy.
To install,

    curl -Ls bit.ly/get-gent | sh

How does GENT work?
===================

Firstly, the `install.sh` script will be fetching a Pack200'ed Groovy jar to bootstraping.
You'll find `~/bin/gent` installed into your system.
To create a new project just call:

    gent <Github Repository>

For example,

    gent genttool/gradle-groovy

The repository `genttool/gradle-groovy.gent` is an example template for building a Groovy project with Gradle.
Note that GENT will be looking for a Github repository with the `.gent` suffix.