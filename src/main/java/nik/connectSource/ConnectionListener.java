package nik.connectSource;

import nik.connectSource.ConnectionData.TransferObject;

public interface ConnectionListener {
    void getTransferObject(Connection connection, TransferObject transferObject);
    void connectionReady(Connection connection);
    void disconnect(Connection connection);
}
