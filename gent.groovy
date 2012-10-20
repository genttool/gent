@GrabResolver(name='jgit-repository', root='http://download.eclipse.org/jgit/maven')
@Grab(group='org.eclipse.jgit', module='org.eclipse.jgit', version='2.1.0.201209190230-r')

import org.eclipse.jgit.api.CommitCommand

println "Running command ${args[0]}"
