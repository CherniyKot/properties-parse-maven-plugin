package org.example;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

@Mojo(name="addchecksum", requiresProject = true)
public class MyMojo extends AbstractMojo
{
    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    private MavenProject project;
    
    @Parameter( defaultValue = "${project.basedir}", required = true, readonly = true)
    private String basedir;
    
    
    public void execute() throws MojoExecutionException
    {
        try {
            File inputFile = new File(basedir+"/target/checksums.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("file");
            
            //StringBuilder result = new StringBuilder(String.format("Checksum:%n"));
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                
                //System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    
                    Element eElement = (Element) nNode;
                    //result.append(String.format("%s:%n",eElement.getAttribute("name")));
                    NodeList t = eElement.getElementsByTagName("hashcode");
                    for (int i = 0; i<t.getLength();i++)
                    {
                        Element tt= (Element) t.item(i);
                        //result.append(String.format("%s:%s%n",tt.getAttribute("algorithm"),tt.getTextContent()));
                        project.getProperties().put(
                                String.format("hashsum.%s.%s",
                                eElement.getAttribute("name").toLowerCase(),
                                tt.getAttribute("algorithm").toLowerCase()),
                                tt.getTextContent());
                    }
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
