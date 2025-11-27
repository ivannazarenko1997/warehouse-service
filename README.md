README — Warehouse Service
📦 Warehouse Service

Reactive UDP Sensor Listener → Kafka Publisher

The Warehouse Service receives measurements from temperature and humidity sensors via UDP, parses them, and publishes normalized sensor events to a message broker (Kafka). Each warehouse runs its own instance of this service.

🔧 Features

Listens for sensor data over UDP

Temperature → 3344

Humidity → 3355

Parses messages in the format:

sensor_id=t1; value=30


Creates structured internal events

Publishes events to Kafka topic:

measurements.events


Reactive non-blocking implementation (Reactor Netty)

Error handling for malformed datagrams

Test coverage for:

Parsing

Mapper conversion

Kafka producer

UDP listener (unit test with mocks)

🗂️ Project Structure
warehouse-service/
 ├── config/           # UDP configs, Kafka configs
 ├── process/          # UDP reactive listener
 ├── kafka/            # Producer + Kafka DTOs
 ├── model/            # Event + datagram models
 ├── mapper/           # Conversion between datagram → event
 ├── service/          # Business services
 ├── domain/           # JPA entities (Sensors)
 ├── controller/       # REST endpoints (alarms)
 └── test/             # Unit tests

📨 UDP Input Format
Sensor Type	Port	Example	Threshold
Temperature	3344	sensor_id=t1; value=30	> 35°C
Humidity	3355	sensor_id=h1; value=40	> 50%
🛠️ Run Warehouse Service
1. Start Kafka & PostgreSQL (optional)
docker compose up -d

2. Run application
./mvnw spring-boot:run


You should see:

UDP listener ready for [TEMPERATURE] on 0.0.0.0:3344
UDP listener ready for [HUMIDITY] on 0.0.0.0:3355

🧪 Testing

Run all tests:

./mvnw test


Sensors can be simulated with Netcat:

echo "sensor_id=t1; value=38" | nc -u -w1 127.0.0.1 3344

🔥 Responsibilities

The Warehouse Service:

Receives datagrams

Parses measurement values

Builds domain events

Publishes them to Kafka

Logs basic information

Does NOT apply thresholds
(this is done by the Central Monitoring Service)
