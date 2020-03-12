package dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JacksonXmlRootElement(localName = "dependency")
public class Dependency {

    @JacksonXmlProperty
    String groupId;
    @JacksonXmlProperty
    String artifactId;
    @JacksonXmlProperty
    String version;

}
