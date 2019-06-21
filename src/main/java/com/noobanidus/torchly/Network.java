package com.noobanidus.torchly;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class Network {
  public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(Torchly.MODID);

  public static void registerPackets() {
    instance.registerMessage(CopyToClipboard.Handler.class, CopyToClipboard.class, 0, Side.CLIENT);
  }

  public static class CopyToClipboard implements IMessage {
    private String clipboard;

    public CopyToClipboard(String clipboard) {
      this.clipboard = clipboard;
    }

    public CopyToClipboard () {
      this.clipboard = "";
    }

    @Override
    public void fromBytes(ByteBuf buf) {
      this.clipboard = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.clipboard);
    }

    public static class Handler implements IMessageHandler<CopyToClipboard, IMessage> {
      @Override
      public IMessage onMessage(CopyToClipboard message, MessageContext ctx) {
        if (Desktop.isDesktopSupported()) {
          StringSelection select = new StringSelection(message.clipboard);
          Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          clipboard.setContents(select, null);
        }
        return null;
      }
    }
  }
}
