package net.coru.kloadgen.extractor.parser.proto;

import static net.coru.kloadgen.extractor.parser.proto.Utils.appendDocumentation;
import static net.coru.kloadgen.extractor.parser.proto.Utils.appendIndented;
import static net.coru.kloadgen.extractor.parser.proto.Utils.checkNotNull;
import static net.coru.kloadgen.extractor.parser.proto.Utils.immutableCopyOf;

import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.coru.kloadgen.extractor.parser.proto.DataType.NamedType;

@AutoValue
public abstract class RpcElement {
  public static Builder builder() {
    return new Builder();
  }

  RpcElement() {
  }

  public abstract String name();
  public abstract String documentation();
  public abstract NamedType requestType();
  public abstract NamedType responseType();
  public abstract List<OptionElement> options();

  public final String toSchema() {
    StringBuilder builder = new StringBuilder();
    appendDocumentation(builder, documentation());
    builder.append("rpc ")
        .append(name())
        .append(" (")
        .append(requestType())
        .append(") returns (")
        .append(responseType())
        .append(')');
    if (!options().isEmpty()) {
      builder.append(" {\n");
      for (OptionElement option : options()) {
        appendIndented(builder, option.toSchemaDeclaration());
      }
      builder.append("}");
    }
    return builder.append(";\n").toString();
  }

  public static final class Builder {
    private String name;
    private String documentation = "";
    private NamedType requestType;
    private NamedType responseType;
    private final List<OptionElement> options = new ArrayList<>();

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

    public Builder requestType(NamedType requestType) {
      this.requestType = checkNotNull(requestType, "requestType");
      return this;
    }

    public Builder responseType(NamedType responseType) {
      this.responseType = checkNotNull(responseType, "responseType");
      return this;
    }

    public Builder addOption(OptionElement option) {
      options.add(checkNotNull(option, "option"));
      return this;
    }

    public Builder addOptions(Collection<OptionElement> options) {
      for (OptionElement option : checkNotNull(options, "options")) {
        addOption(option);
      }
      return this;
    }

    public RpcElement build() {
      checkNotNull(name, "name");
      checkNotNull(requestType, "requestType");
      checkNotNull(responseType, "responseType");

      return new AutoValue_RpcElement(name, documentation, requestType, responseType,
          immutableCopyOf(options));
    }
  }
}
