package org.boot.services.metadata;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.ContentModifyingOperationPreprocessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@AutoConfigureMockMvc
public class BaseControllerTest {

    @Autowired
    protected MockMvc mvc;

    protected RestDocumentationResultHandler restDoc(String name) {
        ContentModifyingOperationPreprocessor jsonSource = new ContentModifyingOperationPreprocessor(new JsonSyntaxHighlighter());
        return document(name,
                preprocessRequest(removeHeaders("Host"),prettyPrint()),
                preprocessResponse(removeHeaders("X-Application-Context","Content-Length"),prettyPrint(),jsonSource));
    }

}
