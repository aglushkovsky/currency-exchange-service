package io.github.aglushkovsky.util;

import io.github.aglushkovsky.exception.ConnectionProviderException;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionProvider {
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final String URL_KEY = "db.url";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static BlockingQueue<Connection> connectionPool;

    static {
        loadDriver();
        initializeConnectionPool();
    }

    private static void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new ConnectionProviderException(e);
        }
    }

    private static void initializeConnectionPool() {
        String poolSizeProperty = PropertiesUtils.getProperty(POOL_SIZE_KEY);
        int poolSize = (poolSizeProperty != null) ? Integer.parseInt(poolSizeProperty) : DEFAULT_POOL_SIZE;
        connectionPool = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Connection connection = openConnection();
            Connection connectionProxy = buildConnectionProxy(connection);
            connectionPool.add(connectionProxy);
        }
    }

    private static Connection buildConnectionProxy(Connection connection) {
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("close")) {
                        return connectionPool.add((Connection) proxy);
                    } else {
                        try {
                            return method.invoke(connection, args);
                        } catch (Exception e) {
                            throw new SQLException(e);
                        }
                    }
                }
            );
    }

    public static Connection getConnection() {
        try {
            return connectionPool.take();
        } catch (InterruptedException e) {
            throw new ConnectionProviderException(e);
        }
    }

    private static Connection openConnection() {
        try {
            return DriverManager.getConnection(PropertiesUtils.getProperty(URL_KEY));
        } catch (SQLException e) {
            throw new ConnectionProviderException(e);
        }
    }

    private ConnectionProvider() {
    }
}
