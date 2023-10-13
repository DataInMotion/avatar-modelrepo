# Example Component

In this bundle we have an immediate component that saves some sample data in the db. 

Data are of type `MedicalRecord` from the `de.avatar.mdp.medical.record.example.model` and of type `Person` from the `de.avatar.mr.example.model`.

The values for their attributes are set via a fake PII generator (`com.github.javafaker`).

Just run the `bnrun` of the bundle, with a running db instance, to save 10 `MedicalRecord` and 10 `Person` objects in the db.