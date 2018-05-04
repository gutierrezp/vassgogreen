package org.example.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoResourceBean;

@HippoEssentialsGenerated(internalName = "gogreen:Author")
@Node(jcrType = "gogreen:Author")
public class Author extends BaseDocument {
	
    @HippoEssentialsGenerated(internalName = "gogreen:name")
    public String getName() {
        return getProperty("gogreen:name");
    }

    @HippoEssentialsGenerated(internalName = "gogreen:email")
    public String getEmail() {
        return getProperty("gogreen:email");
    }

    @HippoEssentialsGenerated(internalName = "gogreen:website")
    public String getWebsite() {
        return getProperty("gogreen:website");
    }

    @HippoEssentialsGenerated(internalName = "gogreen:phoneNumber")
    public String getPhoneNumber() {
        return getProperty("gogreen:phoneNumber");
    }
           
    @HippoEssentialsGenerated(internalName = "gogreen:image")
    public HippoResourceBean getImage() {
        return getBean("gogreen:image", HippoResourceBean.class);
    }
}
