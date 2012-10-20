@GrabResolver(name='jgit-repository', root='http://download.eclipse.org/jgit/maven')
@Grab(group='org.eclipse.jgit', module='org.eclipse.jgit', version='2.0.0.201206130900-r')

import java.io.*

import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.errors.*
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.storage.file.*
import org.eclipse.jgit.transport.*

def cli = new CliBuilder(usage: 'gent [options] [command] <repository>')

cli.with {
    h longOpt: 'help', 'Show help'
    t longOpt: 'template', 'Use template'
    d longOpt: 'name', 'Project name'
}

def options = cli.parse(args)
if (!options) {
    return
}
if(options.h || options.arguments().size() == 0) {
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

switch(cmd) {
    case 'test':
        println System.getProperty("user.dir")
        println System.getProperty("user.home")
        def _ = System.getProperty("file.separator")
        def b = "b"
        println "a$_$b"
        break

    case 'clone':
        def repoPath = options.arguments()[0].replace('/','_')
        def userHome = System.getProperty("user.home")
        def _ = System.getProperty("file.separator")
        def dir = options.d
        if(!dir) {
            dir = options.arguments()[0].split('/')[1]
        }

        CloneCommand clone = Git.cloneRepository();
        clone.setBare(false);
        clone.setNoCheckout(true);
        clone.setURI("git://github.com/${args[0]}.gent.git");
        clone.setDirectory(new File("${userHome}$_.gent$_.repo$_${repoPath}"));
        // TODO clone.setCredentialsProvider(user);
        clone.call();

        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            def workingDir = System.getProperty("user.dir")
            Repository repository = builder
                .setGitDir(new File("${userHome}$_.gent$_.repo$_${repoPath}$_.git"))
                .setWorkTree(new File("${workingDir}$_${dir}"))
                .readEnvironment()
                .build();
            Git git = new Git(repository);
            //
            // git checkout -b master origin/master
            //
            def checkout = git.checkout()
                .setCreateBranch(true)
                .setName("master")
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setStartPoint("origin/master")
            checkout.call()
            println checkout.result.status
        } catch(e) {
            println e.message
        }
        break
}
