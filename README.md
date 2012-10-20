GENT
====

GENT is a project template generator, inspired by conscript and g8, written in Groovy.
To install,

    curl -s https://raw.github.com/chanwit/gent/master/install.sh | sh

How does GENT work?
===================

Firstly, the install.sh script will be fetch a Pack200'ed Groovy jar to bootstraping the system.
You'll find ~/bin/gent installed into your system.
To create a new project just call:

    gent <Github Repository>

For example,

    gent chanwit/gradle-groovy

The repository `chanwit/gradle-groovy` is an example template for building a Groovy project with Gradle.
