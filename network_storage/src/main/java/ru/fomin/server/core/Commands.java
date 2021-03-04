package ru.fomin.server.core;

import ru.fomin.server.network.SocketHandler;
import java.io.IOException;

public interface Commands {
  void handleRequest(String keyCommand, SocketHandler socketHandler) throws IOException;
}
