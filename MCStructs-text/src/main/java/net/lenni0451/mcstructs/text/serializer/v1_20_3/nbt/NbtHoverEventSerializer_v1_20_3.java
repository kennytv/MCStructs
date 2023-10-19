package net.lenni0451.mcstructs.text.serializer.v1_20_3.nbt;

import net.lenni0451.mcstructs.core.Identifier;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;
import net.lenni0451.mcstructs.nbt.tags.IntArrayTag;
import net.lenni0451.mcstructs.nbt.tags.ListTag;
import net.lenni0451.mcstructs.nbt.tags.StringTag;
import net.lenni0451.mcstructs.snbt.SNbtSerializer;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.mcstructs.text.events.hover.AHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.HoverEventAction;
import net.lenni0451.mcstructs.text.events.hover.impl.EntityHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.impl.ItemHoverEvent;
import net.lenni0451.mcstructs.text.events.hover.impl.TextHoverEvent;
import net.lenni0451.mcstructs.text.serializer.ITypedSerializer;

import java.util.UUID;

public class NbtHoverEventSerializer_v1_20_3 implements ITypedSerializer<INbtTag, AHoverEvent> {

    private static final String ACTION = "action";
    private static final String CONTENTS = "contents";
    private static final String VALUE = "value";

    private final ITypedSerializer<INbtTag, ATextComponent> textSerializer;

    public NbtHoverEventSerializer_v1_20_3(final ITypedSerializer<INbtTag, ATextComponent> textSerializer) {
        this.textSerializer = textSerializer;
    }

    @Override
    public INbtTag serialize(AHoverEvent object) {
        return null;
    }

    @Override
    public AHoverEvent deserialize(INbtTag object) {
        if (!object.isCompoundTag()) throw new IllegalArgumentException("Nbt tag is not a compound tag");
        CompoundTag tag = object.asCompoundTag();

        if (!tag.contains(ACTION, NbtType.STRING)) throw new IllegalArgumentException("Expected string tag for '" + ACTION + "' tag");
        HoverEventAction action = HoverEventAction.getByName(tag.getString(ACTION), false);
        if (action == null) throw new IllegalArgumentException("Unknown hover event action: " + tag.getString(ACTION));
        if (!action.isUserDefinable()) throw new IllegalArgumentException("Hover event action is not user definable: " + action);

        if (tag.contains(CONTENTS)) {
            switch (action) {
                case SHOW_TEXT:
                    return new TextHoverEvent(action, this.textSerializer.deserialize(tag.get(CONTENTS)));
                case SHOW_ITEM:
                    //The item id does not have to be a valid item. Minecraft defaults to air if the item is invalid
                    if (tag.contains(CONTENTS, NbtType.STRING)) {
                        return new ItemHoverEvent(action, Identifier.of(tag.getString(CONTENTS)), 1, null);
                    } else if (tag.contains(CONTENTS, NbtType.COMPOUND)) {
                        if (!tag.contains("id", NbtType.STRING)) throw new IllegalArgumentException("Expected string tag for 'id' tag");
                        if (tag.contains("count") && !tag.get("count", new StringTag("")).isNumberTag()) throw new IllegalArgumentException("Expected int tag for 'count' tag");
                        if (tag.contains("tag") && !tag.contains("tag", NbtType.STRING)) throw new IllegalArgumentException("Expected string tag for 'tag' tag");
                        try {
                            String itemTag = tag.getString("tag", null);
                            return new ItemHoverEvent(
                                    action,
                                    Identifier.of(tag.getString("id")),
                                    tag.getInt("count", 1),
                                    itemTag == null ? null : SNbtSerializer.V1_14.deserialize(itemTag)
                            );
                        } catch (Throwable t) {
                            this.sneak(t);
                        }
                    } else {
                        throw new IllegalArgumentException("Expected string or compound tag for '" + CONTENTS + "' tag");
                    }
                case SHOW_ENTITY:
                    if (!tag.contains(CONTENTS, NbtType.COMPOUND)) throw new IllegalArgumentException("Expected compound tag for '" + CONTENTS + "' tag");
                    CompoundTag contents = tag.getCompound(CONTENTS);
                    if (!contents.contains("type", NbtType.STRING)) throw new IllegalArgumentException("Expected string tag for 'type' tag");
                    Identifier type = Identifier.of(contents.getString("type"));
                    UUID id = this.getUUID(tag.get("id"));
                    ATextComponent name = contents.contains("name") ? this.textSerializer.deserialize(contents.get("name")) : null;
                    return new EntityHoverEvent(action, type, id, name);

                default:
                    throw new IllegalArgumentException("Unknown hover event action: " + action);
            }
        } else if (tag.contains(VALUE)) {
            ATextComponent value = this.textSerializer.deserialize(tag.get(VALUE));
            try {
                switch (action) {
                    case SHOW_TEXT:
                        return new TextHoverEvent(action, value);
                    case SHOW_ITEM:
                        CompoundTag parsed = SNbtSerializer.V1_14.deserialize(value.asUnformattedString());
                        Identifier id = Identifier.of(parsed.getString("id"));
                        int count = parsed.getByte("Count");
                        CompoundTag itemTag = parsed.getCompound("tag", null);
                        return new ItemHoverEvent(action, id, count, itemTag);
                    case SHOW_ENTITY:
                        parsed = SNbtSerializer.V1_14.deserialize(value.asUnformattedString());
                        ATextComponent name = null; //TODO: Get the name component from json `jsonComponentParser.parse(parsed.getString("name"))`
                        Identifier type = Identifier.of(parsed.getString("type"));
                        UUID uuid = this.getUUID(parsed.get("id"));
                        return new EntityHoverEvent(action, type, uuid, name);

                    default:
                        throw new IllegalArgumentException("Unknown hover event action: " + action);
                }
            } catch (Throwable t) {
                this.sneak(t);
            }
        }

        throw new IllegalArgumentException("Missing '" + CONTENTS + "' or '" + VALUE + "' tag");
    }

    private <T extends Throwable> void sneak(final Throwable t) throws T {
        throw (T) t;
    }

    private UUID getUUID(final INbtTag tag) {
        if (!(tag instanceof IntArrayTag) && !(tag instanceof ListTag) && !(tag instanceof StringTag)) {
            throw new IllegalArgumentException("Expected int array, list or string tag for 'id' tag");
        }
        int[] value = null;
        if (tag instanceof StringTag) {
            return UUID.fromString(tag.asStringTag().getValue());
        } else if (tag instanceof IntArrayTag) {
            value = tag.asIntArrayTag().getValue();
            if (value.length != 4) throw new IllegalArgumentException("Expected int array with 4 values for 'id' tag");
        } else {
            ListTag<?> list = tag.asListTag();
            if (list.size() != 4) throw new IllegalArgumentException("Expected list with 4 values for 'id' tag");
            if (!list.getType().isNumber()) throw new IllegalArgumentException("Expected list with number values for 'id' tag");
        }
        return new UUID((long) value[0] << 32 | (long) value[1] & 0xFFFF_FFFFL, (long) value[2] << 32 | (long) value[3] & 0xFFFF_FFFFL);
    }

}
