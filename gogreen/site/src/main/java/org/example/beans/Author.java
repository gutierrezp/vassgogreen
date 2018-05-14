package org.example.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoResourceBean;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "author")
@XmlAccessorType(XmlAccessType.NONE)
@HippoEssentialsGenerated(internalName = "gogreen:Author")
@Node(jcrType = "gogreen:Author")
public class Author extends BaseDocument {
    @XmlElement
    @HippoEssentialsGenerated(internalName = "gogreen:name")
    public String getName() {
        return getProperty("gogreen:name");
    }

    @XmlElement
    @HippoEssentialsGenerated(internalName = "gogreen:email")
    public String getEmail() {
        return getProperty("gogreen:email");
    }

    @XmlElement
    @HippoEssentialsGenerated(internalName = "gogreen:website")
    public String getWebsite() {
        return getProperty("gogreen:website");
    }

    @XmlElement
    @HippoEssentialsGenerated(internalName = "gogreen:phoneNumber")
    public String getPhoneNumber() {
        return getProperty("gogreen:phoneNumber");
    }

    @HippoEssentialsGenerated(internalName = "gogreen:image")
    public HippoResourceBean getImage() {
        return getBean("gogreen:image", HippoResourceBean.class);
    }
}
