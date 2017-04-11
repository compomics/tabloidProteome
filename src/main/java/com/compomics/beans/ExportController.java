package com.compomics.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.tomcat.util.http.fileupload.util.Streams;

import com.compomics.neo4j.model.dataTransferObjects.ProteinDTO;

@ManagedBean
@RequestScoped
public class ExportController {

    public void export() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        byte[] exportContent = "Hy Buddys, thanks for the help!".getBytes();
        // here something bad happens that the user should know about
        // but this message does not go out to the user
        fc.addMessage(null, new FacesMessage("record 2 was flawed"));

        ec.responseReset();
        ec.setResponseContentType("text/plain");
        ec.setResponseContentLength(exportContent.length);
        String attachmentName = "attachment; filename=\"export.txt\"";
        ec.setResponseHeader("Content-Disposition", attachmentName);
        try {
            OutputStream output = ec.getResponseOutputStream();
            Streams.copy(new ByteArrayInputStream(exportContent), output, false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        fc.responseComplete();
    }

}