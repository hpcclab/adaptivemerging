package DockerManagement;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pi on 3/15/19.
 */
public class DockerManager {

    public static String imageName = "testimage";

    private static DockerClient docker = null;

    public static List<Container> containers = null;

    private static int port = 5601;

    private static DockerClient CreateDockerClient(){
        return new DefaultDockerClient("unix:///var/run/docker.sock");
        //return new DefaultDockerClient("tcp://localhost:2375");
        //return DefaultDockerClient.builder().uri(URI.create("https://localhost:80")).build();
    }


    public static String CreateContainers(int instanceNum)  {
        String createdIP="";
        for(int i=0;i<instanceNum;i++) {
            createdIP=CreateContainers(9000);
        }
        return createdIP;
    }

    public static String CreateContainers(String givenPort)  {
        String createdIP="";
        if(docker == null) {
            docker = CreateDockerClient();
            System.out.println("docker Client Created");
        }

        String IP;

        try {
            //say it out the ip needed
            BufferedWriter writer = new BufferedWriter(new FileWriter("/mnt/container/portid"));
            writer.write(givenPort);
            writer.close();

            containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());

            final List<Image> images = docker.listImages();

            String[] ports = {givenPort};
            port++;
            System.out.println("givenport="+givenPort);
            final Map<String, List<PortBinding>> portBindings = new HashMap<String, List<PortBinding>>();
            for ( String port : ports ) {
                List<PortBinding> hostPorts = new ArrayList<PortBinding>();
                hostPorts.add( PortBinding.of( "", givenPort ) ); //host to container port
                portBindings.put( givenPort, hostPorts ); //+ "/tcp" is optional?
            }

            final HostConfig hostConfig = HostConfig.builder()
                    .binds("/mnt/container:/home/shared")
                    .portBindings(portBindings)
                    .build();

            final String[] command = {"/bin/bash"};
            final ContainerConfig containerConfig = ContainerConfig.builder()
                    .image(imageName)
                    .attachStderr(Boolean.TRUE)
                    .attachStdin(Boolean.TRUE)
                    .tty(Boolean.TRUE)
                    .hostConfig(hostConfig)
                    .exposedPorts( givenPort) //container to host + "/tcp"
                    .cmd(command)
                    .build();

                final ContainerCreation containerCreation = docker.createContainer(containerConfig);
                docker.startContainer(containerCreation.id());
                createdIP+=docker.inspectContainer(containerCreation.id()).networkSettings().ipAddress()+",";
                createdIP="0.0.0.0"; //TESTTTT, not using the returned IP
                //byte[] ByteCode=givenPort.getBytes();
                //Files.write("/mnt/container/portid",ByteCode);



        }catch(Exception e){
            System.out.println("Docker fail: "+e);
        }
        return createdIP;
    }

    //stop all containers?
    public static void KillAllContainers() throws DockerException, InterruptedException {
        if(docker == null)
            docker = CreateDockerClient();

        containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());

        for (int i=0;i<containers.size();i++){
            docker.killContainer(containers.get(i).id());
        }
    }

    //permanently remove all containers
    public static void RemoveAllContainers() throws DockerException, InterruptedException {
        if(docker == null)
            docker = CreateDockerClient();

        containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());

        for (int i=0;i<containers.size();i++){
            docker.stopContainer(containers.get(i).id(), 0);
            docker.removeContainer(containers.get(i).id());
        }
    }

    public static void ExtractFiles(){

    }

    public static void GetCurrentContainers() throws DockerException, InterruptedException {
        if(docker == null)
            docker = CreateDockerClient();

        containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());
    }

}

