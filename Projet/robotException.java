public class robotException extends Exception {
    public robotException(String message) {
        super(message);
    }
}
class energieInsuffisanteException extends robotException {
    public energieInsuffisanteException(String message) {
        super(message);
    }
}
class maintenanceException extends robotException {
    public maintenanceException(String message) {
        super(message);
    }
}

