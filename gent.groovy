@GrabResolver(name='jgit-repository', root='http://download.eclipse.org/jgit/maven')
@Grab(group='org.eclipse.jgit', module='org.eclipse.jgit', version='2.0.0.201206130900-r')

import java.io.*

import groovy.util.*

import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.errors.*
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.storage.file.*
import org.eclipse.jgit.transport.*

def cli = new CliBuilder(usage:'gent [options] [command] <repository>')
cli.t(longOpt:'template', 'Template')
def opts = cli.parse(args)
if(!opts) {
    println cli.usage()
    return
}

def cmd = options.arguments()[0]
def commands = ['test','create']
if(cmd in commands) {
    // do nothing
} else {
    cmd = 'clone'
}

/*
def home
if(windows) {
    home =
} else {
    home =
}
*/

switch(cmd) {
    case 'test':
        println System.getProperty("user.dir")
        break

    case 'clone':
        def repoPath = args[0].replace('/','_')

        CloneCommand clone = Git.cloneRepository();
        clone.setBare(false);
        clone.setNoCheckout(true);
        clone.setURI("git://github.com/${args[0]}.gent.git");
        clone.setDirectory(new File("C:\\Users\\chanwit\\.gent\\.repo\\${repoPath}"));
        // clone.setCredentialsProvider(user);
        clone.call();

        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            def workingDir = System.getProperty("user.dir")
            Repository repository = builder
                .setGitDir(new File("C:\\Users\\chanwit\\.gent\\.repo\\${repoPath}\\.git"))
                .setWorkTree(new File(workingDir))
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
