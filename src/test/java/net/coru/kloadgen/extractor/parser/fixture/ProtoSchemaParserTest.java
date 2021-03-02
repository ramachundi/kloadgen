package net.coru.kloadgen.extractor.parser.fixture;

import static net.coru.kloadgen.extractor.parser.fixture.JsonSchemaFixturesConstants.SIMPLE_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import net.coru.kloadgen.extractor.parser.impl.ProtoSchemaParser;
import net.coru.kloadgen.model.json.Schema;
import net.coru.kloadgen.testutil.FileHelper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
class ProtoSchemaParserTest {

  private static final FileHelper resourceAsFile = new FileHelper();

  private static final ProtoSchemaParser protoSchemaParser = new ProtoSchemaParser();

  private static Stream<Arguments> parametersForShouldParseProtoSchemaDocument() throws Exception {
    return Stream.of(
        Arguments.of(resourceAsFile.getContent("/protoschema/basic.proto"), SIMPLE_SCHEMA)
    );
  }

  @ParameterizedTest
  @MethodSource("parametersForShouldParseProtoSchemaDocument")
  void shouldParseProtoSchemaDocument(String schemaAsProto, Schema expected) {
    log.info(schemaAsProto);
    Schema result = protoSchemaParser.parse(schemaAsProto);
    assertThat(result).isEqualTo(expected);
  }

}
