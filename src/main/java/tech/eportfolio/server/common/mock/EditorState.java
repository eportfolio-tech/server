package tech.eportfolio.server.common.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import tech.eportfolio.server.common.utility.ParagraphProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generated from JSON using http://www.jsonschema2pojo.org/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "key",
        "text",
        "type",
        "depth",
        "inlineStyleRanges",
        "entityRanges",
        "data"
})

@Builder
class Block {
    @JsonProperty("key")
    public String key;
    @JsonProperty("text")
    public String text;
    @JsonProperty("type")
    public String type;
    @JsonProperty("depth")
    public int depth;
    @JsonProperty("inlineStyleRanges")
    public List<InlineStyleRange> inlineStyleRanges;
    @JsonProperty("entityRanges")
    public List<Object> entityRanges;
    @JsonProperty("data")
    public Object data;

}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({

})
class EntityMap {


}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "offset",
        "length",
        "style"
})
class InlineStyleRange {
    @JsonProperty("offset")
    public int offset;
    @JsonProperty("length")
    public int length;
    @JsonProperty("style")
    public String style;
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "blocks",
        "entityMap"
})

@Data
@Builder
public class EditorState {
    @JsonProperty("blocks")
    public List<Block> blocks = null;
    @JsonProperty("entityMap")
    public EntityMap entityMap;


    public static EditorState generateState() {
        Block block = Block.builder().
                depth(0).
                entityRanges(new ArrayList<>()).
                inlineStyleRanges(new ArrayList<>()).
                key(RandomStringUtils.randomAlphanumeric(5).toLowerCase()).
                type("unstyled").
                data(null).
                text(ParagraphProvider.paragraph())
                .build();
        return EditorState.builder().blocks(Collections.singletonList(block)).entityMap(new EntityMap()).build();
    }
}
