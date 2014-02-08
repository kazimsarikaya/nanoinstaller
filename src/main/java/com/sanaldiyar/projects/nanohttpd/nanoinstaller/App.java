/*
Nano System Installer
Copryright © 2014 Kazım SARIKAYA

This program is licensed under the terms of Sanal Diyar Software License. Please
read the license file or visit http://license.sanaldiyar.com
*/
package com.sanaldiyar.projects.nanohttpd.nanoinstaller;

import com.jcabi.aether.Aether;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        try {
            String executableName = new File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
            Options options = new Options();

            Option destination = OptionBuilder.withArgName("folder")
                    .withLongOpt("destination")
                    .hasArgs(1)
                    .withDescription("destionation folder")
                    .withType(String.class)
                    .create("d");

            Option lrfolder = OptionBuilder.withArgName("folder")
                    .withLongOpt("localrepo")
                    .hasArgs(1)
                    .withDescription("local repository folder")
                    .withType(String.class)
                    .create("lr");

            Option rmlrfolder = OptionBuilder.withLongOpt("deletelocalrepo")
                    .hasArg(false)
                    .withDescription("delete local repository after installation")
                    .create("dlr");

            Option help = OptionBuilder.withLongOpt("help")
                    .withDescription("print this help")
                    .create("h");

            options.addOption(destination);
            options.addOption(lrfolder);
            options.addOption(rmlrfolder);
            options.addOption(help);

            HelpFormatter helpFormatter = new HelpFormatter();

            CommandLineParser commandLineParser = new PosixParser();
            CommandLine commands;
            try {
                commands = commandLineParser.parse(options, args);
            } catch (ParseException ex) {
                System.out.println("Error at parsing arguments");
                helpFormatter.printHelp("java -jar " + executableName, options);
                return;
            }

            if (commands.hasOption("h")) {
                helpFormatter.printHelp("java -jar " + executableName, options);
                return;
            }

            String sdest = commands.getOptionValue("d", "./nanosystem");
            System.out.println("The nano system will be installed into " + sdest);
            File dest = new File(sdest);
            if (dest.exists()) {
                FileUtils.deleteDirectory(dest);
            }
            dest.mkdirs();
            File bin = new File(dest, "bin");
            bin.mkdir();
            File bundle = new File(dest, "bundle");
            bundle.mkdir();
            File conf = new File(dest, "conf");
            conf.mkdir();
            File core = new File(dest, "core");
            core.mkdir();
            File logs = new File(dest, "logs");
            logs.mkdir();
            File nanohttpdcore = new File(dest, "nanohttpd-core");
            nanohttpdcore.mkdir();
            File nanohttpdservices = new File(dest, "nanohttpd-services");
            nanohttpdservices.mkdir();
            File temp = new File(dest, "temp");
            temp.mkdir();
            File apps = new File(dest, "apps");
            apps.mkdir();

            File local = new File(commands.getOptionValue("lr", "./local-repository"));
            Collection<RemoteRepository> repositories = Arrays.asList(new RemoteRepository("sanaldiyar-snap", "default", "http://maven2.sanaldiyar.com/snap-repo"),
                    new RemoteRepository("central", "default", "http://repo1.maven.org/maven2/"));
            Aether aether = new Aether(repositories, local);

            //Copy core felix main
            System.out.println("Downloading Felix main executable");
            List<Artifact> felixmain = aether.resolve(new DefaultArtifact("org.apache.felix", "org.apache.felix.main", "jar", "LATEST"), "runtime");
            for (Artifact artifact : felixmain) {
                if (artifact.getArtifactId().equals("org.apache.felix.main")) {
                    FileUtils.copyFile(artifact.getFile(), new File(bin, "felix-main.jar"));
                    System.out.println(artifact.getArtifactId());
                    break;
                }
            }
            System.out.println("OK");

            //Copy core felix bundles
            System.out.println("Downloading Felix core bundles");
            Collection<String> felixcorebundles = Arrays.asList("fileinstall", "bundlerepository", "gogo.runtime", "gogo.shell", "gogo.command");
            for (String felixcorebunlde : felixcorebundles) {
                List<Artifact> felixcore = aether.resolve(new DefaultArtifact("org.apache.felix", "org.apache.felix." + felixcorebunlde, "jar", "LATEST"), "runtime");
                for (Artifact artifact : felixcore) {
                    if (artifact.getArtifactId().equals("org.apache.felix." + felixcorebunlde)) {
                        FileUtils.copyFileToDirectory(artifact.getFile(), core);
                        System.out.println(artifact.getArtifactId());
                    }
                }
            }
            System.out.println("OK");

            //Copy nanohttpd core bundles
            System.out.println("Downloading nanohttpd core bundles and configurations");
            List<Artifact> nanohttpdcorebundle = aether.resolve(new DefaultArtifact("com.sanaldiyar.projects.nanohttpd", "nanohttpd", "jar", "LATEST"), "runtime");
            for (Artifact artifact : nanohttpdcorebundle) {
                if (!artifact.getArtifactId().equals("org.osgi.core")) {
                    FileUtils.copyFileToDirectory(artifact.getFile(), nanohttpdcore);
                    System.out.println(artifact.getArtifactId());
                }
            }

            nanohttpdcorebundle = aether.resolve(new DefaultArtifact("com.sanaldiyar.projects", "engender", "jar", "LATEST"), "runtime");
            for (Artifact artifact : nanohttpdcorebundle) {
                FileUtils.copyFileToDirectory(artifact.getFile(), nanohttpdcore);
                System.out.println(artifact.getArtifactId());
            }

            nanohttpdcorebundle = aether.resolve(new DefaultArtifact("org.codehaus.jackson", "jackson-mapper-asl", "jar", "1.9.5"), "runtime");
            for (Artifact artifact : nanohttpdcorebundle) {
                FileUtils.copyFileToDirectory(artifact.getFile(), nanohttpdcore);
                System.out.println(artifact.getArtifactId());
            }

            nanohttpdcorebundle = aether.resolve(new DefaultArtifact("org.mongodb", "mongo-java-driver", "jar", "LATEST"), "runtime");
            for (Artifact artifact : nanohttpdcorebundle) {
                FileUtils.copyFileToDirectory(artifact.getFile(), nanohttpdcore);
                System.out.println(artifact.getArtifactId());
            }

            //Copy nanohttpd conf
            FileUtils.copyInputStreamToFile(App.class.getResourceAsStream("/nanohttpd.conf"), new File(dest, "nanohttpd.conf"));
            System.out.println("Configuration: nanohttpd.conf");

            //Copy nanohttpd start script
            File startsh = new File(dest, "start.sh");
            FileUtils.copyInputStreamToFile(App.class.getResourceAsStream("/start.sh"), startsh);
            startsh.setExecutable(true);
            System.out.println("Script: start.sh");

            System.out.println("OK");

            //Copy nanohttpd service bundles
            System.out.println("Downloading nanohttpd service bundles");
            List<Artifact> nanohttpdservicebundle = aether.resolve(new DefaultArtifact("com.sanaldiyar.projects.nanohttpd", "mongodbbasedsessionhandler", "jar", "1.0-SNAPSHOT"), "runtime");
            for (Artifact artifact : nanohttpdservicebundle) {
                if (artifact.getArtifactId().equals("mongodbbasedsessionhandler")) {
                    FileUtils.copyFileToDirectory(artifact.getFile(), nanohttpdservices);
                    System.out.println(artifact.getArtifactId());
                    break;
                }
            }

            //Copy nanohttpd mongodbbasedsessionhandler conf
            FileUtils.copyInputStreamToFile(App.class.getResourceAsStream("/mdbbasedsh.conf"), new File(dest, "mdbbasedsh.conf"));
            System.out.println("Configuration: mdbbasedsh.conf");

            System.out.println("OK");

            if (commands.hasOption("dlr")) {
                System.out.println("Local repository is deleting");
                FileUtils.deleteDirectory(local);
                System.out.println("OK");
            }

            System.out.println("You can reconfigure nanohttpd and services. To start system run start.sh script");

        } catch (Exception ex) {
            System.out.println("Error at installing.");
        }
    }

}
