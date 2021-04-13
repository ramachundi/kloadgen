package net.coru.kloadgen.extractor.parser.proto;

import static net.coru.kloadgen.extractor.parser.proto.Utils.appendDocumentation;
import static net.coru.kloadgen.extractor.parser.proto.Utils.appendIndented;
import static net.coru.kloadgen.extractor.parser.proto.Utils.checkNotNull;
import static net.coru.kloadgen.extractor.parser.proto.Utils.immutableCopyOf;

import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AutoValue
public abstract class OneOfElement {
  public static Builder builder() {
    return new Builder();
  }

  OneOfElement() {
  }

  public abstract String name();
  public abstract String documentation();
  public abstract List<FieldElement> fields();

  public final String toSchema() {
    StringBuilder builder = new StringBuilder();
    appendDocumentation(builder, documentation());
    builder.append("oneof ").append(name()).append(" {");
    if (!fields().isEmpty()) {
      builder.append('\n');
      for (FieldElement field : fields()) {
        appendIndented(builder, field.toSchema());
      }
    }
    return builder.append("}\n").toString();
  }

  public static final class Builder {
    private String name;
    private String documentation = "";
    private final List<FieldElement> fields = new ArrayList<>();

    private Builder() {
    }

    public Builder name(String name) {
      this.name = checkNotNull(name, "name");
      return this;
    }

    public Builder documentation(String documentation) {
      this.documentation = checkNotNull(documentation, "documentation");
      return this;
    }

    public Builder addField(FieldElement field) {
      fields.add(checkNotNull(field, "field"));
      return this;
    }

    public Builder addFields(Collection<FieldElement> fields) {
      for (FieldElement field : checkNotNull(fields, "fields")) {
        addField(field);
      }
      return this;
    }

    public OneOfElement build() {
      checkNotNull(name, "name");
      // TODO check non-empty?

      return new AutoValue_OneOfElement(name, documentation, immutableCopyOf(fields));
    }
  }
}
