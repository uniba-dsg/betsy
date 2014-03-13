package configuration

import betsy.data.BPMNProcess
import betsy.repositories.Repository

class BPMNProcessRepository {
    private Repository<BPMNProcess> repo = new Repository<>()

    public BPMNProcessRepository(){

    }

    public List<BPMNProcess> getByName(String name) {
        return repo.getByName(name);
    }

    public List<BPMNProcess> getByNames(String[] names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }
}
