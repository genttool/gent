@GrabResolver(name='jgit-repository', root='http://download.eclipse.org/jgit/maven')
@Grab(group='org.eclipse.jgit', module='org.eclipse.jgit', version='2.0.0.201206130900-r')

import java.io.*

import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.errors.*
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.storage.file.*
import org.eclipse.jgit.transport.*

def commands = ['test','create']
def cmd = ''
if(args[0] in commands) {
    cmd = args[0]
} else {
    cmd = 'clone'
}

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
        clone.setDirectory(new File("C:\\Users\\chanwit\\.gent\\.metadata\\${repoPath}"));
        // clone.setCredentialsProvider(user);
        clone.call();

        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            def workingDir = System.getProperty("user.dir")
            Repository repository = builder
                .setGitDir(new File("C:\\Users\\chanwit\\.gent\\.metadata\\${repoPath}\\.git"))
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
