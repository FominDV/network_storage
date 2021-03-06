package ru.fomin.core;

import ru.fomin.network.SocketHandler;
import java.io.IOException;

public interface Commands {
  void handleRequest(String keyCommand, SocketHandler socketHandler) throws IOException;
}
