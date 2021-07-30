package net.coru.kloadgen.extractor.parser.proto;

import static org.assertj.core.api.Assertions.assertThat;

import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.ANY;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.BOOL;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.BYTES;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.DOUBLE;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.FIXED32;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.FIXED64;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.FLOAT;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.INT32;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.INT64;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.SFIXED32;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.SFIXED64;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.SINT32;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.SINT64;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.STRING;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.UINT32;
import static net.coru.kloadgen.extractor.parser.proto.DataType.ScalarType.UINT64;
import static net.coru.kloadgen.extractor.parser.proto.FieldElement.Label.REQUIRED;

import net.coru.kloadgen.extractor.SchemaExtractor;
import net.coru.kloadgen.extractor.impl.SchemaExtractorImpl;
import net.coru.kloadgen.extractor.parser.proto.DataType.MapType;
import net.coru.kloadgen.extractor.parser.proto.DataType.NamedType;
import org.junit.jupiter.api.Test;

class ProtoParserTest {

  private final SchemaExtractor schemaExtractor = new SchemaExtractorImpl();

  @Test
  void protoParserTest() {
    String proto = ""
        + "message Types {\n"
        + "  required any f1 = 1;\n"
        + "  required bool f2 = 2;\n"
        + "  required bytes f3 = 3;\n"
        + "  required double f4 = 4;\n"
        + "  required float f5 = 5;\n"
        + "  required fixed32 f6 = 6;\n"
        + "  required fixed64 f7 = 7;\n"
        + "  required int32 f8 = 8;\n"
        + "  required int64 f9 = 9;\n"
        + "  required sfixed32 f10 = 10;\n"
        + "  required sfixed64 f11 = 11;\n"
        + "  required sint32 f12 = 12;\n"
        + "  required sint64 f13 = 13;\n"
        + "  required string f14 = 14;\n"
        + "  required uint32 f15 = 15;\n"
        + "  required uint64 f16 = 16;\n"
        + "  required map<string, bool> f17 = 17;\n"
        + "  required map<arbitrary, nested.nested> f18 = 18;\n"
        + "  required arbitrary f19 = 19;\n"
        + "  required nested.nested f20 = 20;\n"
        + "}\n";
    ProtoFile expected = ProtoFile.builder("test.proto")
        .addType(MessageElement.builder()
            .name("Types")
            .addField(FieldElement.builder().label(REQUIRED).type(ANY).name("f1").tag(1).build())
            .addField(FieldElement.builder().label(REQUIRED).type(BOOL).name("f2").tag(2).build())
            .addField(FieldElement.builder().label(REQUIRED).type(BYTES).name("f3").tag(3).build())
            .addField(FieldElement.builder().label(REQUIRED).type(DOUBLE).name("f4").tag(4).build())
            .addField(FieldElement.builder().label(REQUIRED).type(FLOAT).name("f5").tag(5).build())
            .addField(
                FieldElement.builder().label(REQUIRED).type(FIXED32).name("f6").tag(6).build())
            .addField(
                FieldElement.builder().label(REQUIRED).type(FIXED64).name("f7").tag(7).build())
            .addField(FieldElement.builder().label(REQUIRED).type(INT32).name("f8").tag(8).build())
            .addField(FieldElement.builder().label(REQUIRED).type(INT64).name("f9").tag(9).build())
            .addField(
                FieldElement.builder().label(REQUIRED).type(SFIXED32).name("f10").tag(10).build())
            .addField(
                FieldElement.builder().label(REQUIRED).type(SFIXED64).name("f11").tag(11).build())
            .addField(
                FieldElement.builder().label(REQUIRED).type(SINT32).name("f12").tag(12).build())
            .addField(
                FieldElement.builder().label(REQUIRED).type(SINT64).name("f13").tag(13).build())
            .addField(
                FieldElement.builder().label(REQUIRED).type(STRING).name("f14").tag(14).build())
            .addField(
                FieldElement.builder().label(REQUIRED).type(UINT32).name("f15").tag(15).build())
            .addField(
                FieldElement.builder().label(REQUIRED).type(UINT64).name("f16").tag(16).build())
            .addField(FieldElement.builder()
                .label(REQUIRED)
                .type(MapType.create(STRING, BOOL))
                .name("f17")
                .tag(17)
                .build())
            .addField(FieldElement.builder()
                .label(REQUIRED)
                .type(MapType.create(NamedType.create("arbitrary"),
                    NamedType.create("nested.nested")))
                .name("f18")
                .tag(18)
                .build())
            .addField(FieldElement.builder()
                .label(REQUIRED)
                .type(NamedType.create("arbitrary"))
                .name("f19")
                .tag(19)
                .build())
            .addField(FieldElement.builder()
                .label(REQUIRED)
                .type(NamedType.create("nested.nested"))
                .name("f20")
                .tag(20)
                .build())
            .build())
        .build();
    assertThat(ProtoParserKload.parse("test.proto", proto)).isEqualTo(expected);

  }

}
