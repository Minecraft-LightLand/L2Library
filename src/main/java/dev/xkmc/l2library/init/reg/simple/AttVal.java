package dev.xkmc.l2library.init.reg.simple;

import dev.xkmc.l2library.capability.attachment.AttachmentDef;
import net.neoforged.neoforge.attachment.AttachmentType;

public interface AttVal<T> extends Val<AttachmentType<T>> {

	AttachmentDef<T> type();

}
