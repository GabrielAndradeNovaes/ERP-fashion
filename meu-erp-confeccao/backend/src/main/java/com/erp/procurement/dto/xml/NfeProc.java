package com.erp.procurement.dto.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NfeProc {
    
    @JacksonXmlProperty(localName = "NFe")
    public NFe nfe;

    @JacksonXmlProperty(localName = "protNFe")
    public ProtNFe protNFe;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NFe {
        @JacksonXmlProperty(localName = "infNFe")
        public InfNFe infNFe;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProtNFe {
        @JacksonXmlProperty(localName = "infProt")
        public InfProt infProt;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InfProt {
        @JacksonXmlProperty(localName = "chNFe")
        public String chNFe;
        @JacksonXmlProperty(localName = "cStat")
        public String cStat;
        @JacksonXmlProperty(localName = "xMotivo")
        public String xMotivo;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InfNFe {
        @JacksonXmlProperty(localName = "Id", isAttribute = true)
        public String id;
        
        @JacksonXmlProperty(localName = "ide")
        public Ide ide;
        
        @JacksonXmlProperty(localName = "emit")
        public Emit emit;

        @JacksonXmlProperty(localName = "dest")
        public Dest dest;
        
        @JacksonXmlProperty(localName = "total")
        public Total total;
        
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "det")
        public List<Det> det;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Ide {
        @JacksonXmlProperty(localName = "nNF")
        public String nNF;
        @JacksonXmlProperty(localName = "dhEmi")
        public String dhEmi;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Emit {
        @JacksonXmlProperty(localName = "CNPJ")
        public String cnpj;
        @JacksonXmlProperty(localName = "CPF")
        public String cpf;
        @JacksonXmlProperty(localName = "xNome")
        public String xNome;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dest {
        @JacksonXmlProperty(localName = "CNPJ")
        public String cnpj;
        @JacksonXmlProperty(localName = "CPF")
        public String cpf;
        @JacksonXmlProperty(localName = "xNome")
        public String xNome;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Total {
        @JacksonXmlProperty(localName = "ICMSTot")
        public ICMSTot icmsTot;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ICMSTot {
        @JacksonXmlProperty(localName = "vNF")
        public String vNF;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Det {
        @JacksonXmlProperty(localName = "prod")
        public Prod prod;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Prod {
        @JacksonXmlProperty(localName = "cProd")
        public String cProd;
        @JacksonXmlProperty(localName = "xProd")
        public String xProd;
        @JacksonXmlProperty(localName = "NCM")
        public String ncm;
        @JacksonXmlProperty(localName = "uCom")
        public String uCom;
        @JacksonXmlProperty(localName = "qCom")
        public String qCom;
        @JacksonXmlProperty(localName = "vUnCom")
        public String vUnCom;
        @JacksonXmlProperty(localName = "vProd")
        public String vProd;
    }
}
