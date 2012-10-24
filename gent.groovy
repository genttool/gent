@GrabResolver(name='jgit-repository', root='http://download.eclipse.org/jgit/maven')
@Grab(group='org.eclipse.jgit', module='org.eclipse.jgit', version='2.0.0.201206130900-r')
@Grab(group='commons-io', module='commons-io', version='2.4')

import java.io.*

import java.security.MessageDigest

import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.errors.*
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.storage.file.*
import org.eclipse.jgit.transport.*

import org.apache.commons.io.*

import java.util.Properties
import groovy.text.*

def cli = new CliBuilder(usage: 'gent [options] [command] <template name>')
cli.header = 'GENT tool (c) 2012 the GENT Project and contributors.'
cli.with {
    h longOpt: 'help', 'Show help'
    d longOpt: 'name', args:1, argName:'dir', 'Apply the template to the target directory'
    _ longOpt: 'http',  'Use HTTP protocol instead of GIT'
    _ longOpt: 'https', 'Use HTTPS protocol instead of GIT'
}

//
// make options float in-front-of commands
//
def options = cli.parse(args.sort { a, b ->
    if(a.startsWith("-")) return -1
    if(b.startsWith("-")) return  1
    return 0
})

if (!options) {
    return
}
if(options['h'] || options.arguments().size() == 0) {
    cli.usage()
    return
}

def cmd = options.arguments()[0]
def commands = ['test','create']
if(cmd in commands) {
    // do nothing
} else {
    cmd = 'clone'
}

def isWindows = {-> System.getProperty("os.name").startsWith("Windows") }

def md5 = { str ->
    MessageDigest md = MessageDigest.getInstance("MD5")
    md.update(str.toString().getBytes())
    BigInteger hash = new BigInteger(1, md.digest())
    return hash.toString(16)
}

switch(cmd) {
    case 'test':
        println System.getProperty("user.dir")
        println System.getProperty("user.home")
        def _ = System.getProperty("file.separator")
        def b = "b"
        println "a$_$b"
        println options['d']
        break

    case 'clone':
        def a = options.arguments()
        def pathIndex
        if(a.size() == 1) {
            pathIndex = 0
        }
        else {
            pathIndex = 1
        }

        def remoteRepoPath = a[pathIndex]

        //
        // if there's .gent suffix, remove it
        //
        if(remoteRepoPath.endsWith('.gent')) {
            remoteRepoPath = remoteRepoPath - '.gent'
        }

        //
        // in case of no repo prefix, use the defult username "genttool"
        //
        if(remoteRepoPath.contains('/') == false) {
            remoteRepoPath = "genttool/${remoteRepoPath}"
        }

        def userHome = System.getProperty("user.home")
        def _ = System.getProperty("file.separator")
        def dir = options['d']
        if(!dir) {
            dir = remoteRepoPath.split('/')[1]
        }

        def workingDir = System.getProperty("user.dir")
        def targetDir = "${workingDir}${_}${dir}"
        def hash = md5(targetDir)

        CloneCommand clone = Git.cloneRepository()
        clone.setBare(false);
        clone.setNoCheckout(true);
        if(options['http']) {
            clone.setURI("http://github.com/${remoteRepoPath}.gent.git")
        } else if(options['https']) {
            clone.setURI("https://github.com/${remoteRepoPath}.gent.git")
        } else {
            clone.setURI("git://github.com/${remoteRepoPath}.gent.git")
        }
        clone.setDirectory(new File("${userHome}${_}.gent${_}.repo${_}${hash}"))
        // TODO clone.setCredentialsProvider(user);
        def g = clone.call()
        g.getRepository().close()

        Repository repository = null
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder()
            repository = builder
                .setGitDir(new File("${userHome}${_}.gent${_}.repo${_}${hash}${_}.git"))
                .setWorkTree(new File(targetDir))
                .readEnvironment()
                .build()
            Git git = new Git(repository)

            //
            // git checkout origin/master
            //
            def checkout = git.checkout()
                .setCreateBranch(false)
                .setName('origin/master')
                .setStartPoint('origin/master')
                .setForce(true)
            checkout.call()

            //
            // delete all .gitignore "MARKER" files whose size() is 0
            //
            def files = FileUtils.listFiles(new File(targetDir), ['gitignore'] as String[], true)
            files.each { if(it.length() == 0) FileUtils.forceDelete(it) }

            //
            // load properties
            //
            def binding = new Properties()
            def propFile = new File("${targetDir}${_}gent.properties")
            def propRader = propFile.newReader('UTF-8')
            binding.load(propRader)
            binding.setProperty('name', dir)
            binding.setProperty('now',  new Date().toString())
            propRader.close()

            //
            // delete gent.properties
            //
            FileUtils.forceDelete(propFile)

            //
            // scan all .in files
            // they are template
            //
            def templates = FileUtils.listFiles(new File(targetDir), ['in'] as String[], true)
            templates.each {
                def engine = new GStringTemplateEngine()
                def tmplReader = it.newReader('UTF-8')
                def template = new GStringTemplateEngine().createTemplate(tmplReader)
                FileUtils.writeStringToFile(
                    new File(it.getAbsolutePath() - '.in'),
                    template.make(binding).toString(),
                    "UTF-8")
                tmplReader.close()
                // delete .in file
                FileUtils.forceDelete(it)
            }

            if(binding['welcomeMessage']) {
                println()
                println binding['welcomeMessage']
            }

        } catch(e) {
            println e.message
        } finally {
            repository.close()
            FileUtils.forceDelete(new File("${userHome}${_}.gent${_}.repo${_}${hash}"))
        }
        break
}
