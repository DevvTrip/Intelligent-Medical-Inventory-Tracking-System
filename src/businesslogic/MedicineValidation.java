package businesslogic;

import model.Medicine;

public class MedicineValidation {
    public static void check(Medicine medicine) throws InvalidInputException {
        try {
            if (Integer.parseInt(medicine.getId()) <= 0) {
                throw new InvalidInputException("ID must be a positive number");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException("ID must be a positive number");
        }
        if (medicine.getName().trim().isEmpty()
                || medicine.getSupplier().getName().trim().isEmpty()) {
            throw new InvalidInputException("Medicine name and supplier name cannot be empty");
        }
        if (medicine.getPrice() < 0 || medicine.getQty() < 0
                || medicine.getThreshold() < 1 || medicine.getExtra() < 1) {
            throw new InvalidInputException("Use a valid price, non-negative quantity and positive limit/extra");
        }
        // comma allowed nahi hai, kyunki CSV me comma se hi fields alag hote hain
        String[] text = {medicine.getName(), medicine.getSupplier().getName(),
                medicine.getSupplier().getPhone()};
        for (String value : text) {
            if (value.contains(",")) {
                throw new InvalidInputException("Do not use commas in text fields");
            }
        }
    }
}
