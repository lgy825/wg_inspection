package com.raisecom.util;

import com.raisecom.nms.platform.cnet.ObjService;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;

/**
 * Created by ligy-008494 on 2018/11/14.
 */
public class XMLFileHelperUtil {


    public static final ObjService loadConfigInObjService(String path) {
        Document doc = null;
        try {
            doc = new SAXBuilder().build(path);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createObjService(doc.getRootElement());
    }

    private static ObjService createObjService(Element element) {
        ObjService obj = new ObjService(element.getName());
        for (Object a : element.getAttributes()) {
            Attribute attribute = (Attribute) a;
            obj.setValue(attribute.getName(), attribute.getValue());
        }
        for (Object child : element.getChildren()) {
            obj.addContainedObject(createObjService((Element) child));
        }
        return obj;
    }
}
