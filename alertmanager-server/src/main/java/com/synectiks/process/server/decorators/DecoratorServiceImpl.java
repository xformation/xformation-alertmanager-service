/*
 * */
package com.synectiks.process.server.decorators;

import com.google.common.base.Strings;
import com.mongodb.DBCollection;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.CollectionName;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.NotFoundException;

import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class DecoratorServiceImpl implements DecoratorService {
    private final JacksonDBCollection<DecoratorImpl, String> coll;

    @Inject
    public DecoratorServiceImpl(MongoConnection mongoConnection, MongoJackObjectMapperProvider mongoJackObjectMapperProvider) {
        final String collectionName = DecoratorImpl.class.getAnnotation(CollectionName.class).value();
        final DBCollection dbCollection = mongoConnection.getDatabase().getCollection(collectionName);
        this.coll = JacksonDBCollection.wrap(dbCollection, DecoratorImpl.class, String.class, mongoJackObjectMapperProvider.get());
    }

    @Override
    public List<Decorator> findForStream(String streamId) {
        return toInterfaceList(coll.find(DBQuery.is(DecoratorImpl.FIELD_STREAM, Optional.of(streamId))).toArray());
    }

    @Override
    public List<Decorator> findForGlobal() {
        return toInterfaceList(coll.find(DBQuery.or(
            DBQuery.notExists(DecoratorImpl.FIELD_STREAM),
            DBQuery.is(DecoratorImpl.FIELD_STREAM, Optional.empty())
        )).toArray());
    }

    @Override
    public Decorator findById(String decoratorId) throws NotFoundException {
        final Decorator result = coll.findOneById(decoratorId);
        if (result == null) {
            throw new NotFoundException("Decorator with id " + decoratorId + " not found.");
        }

        return result;
    }

    @Override
    public List<Decorator> findAll() {
        return toInterfaceList(coll.find().toArray());
    }

    @Override
    public Decorator create(String type, Map<String, Object> config, String stream, int order) {
        return DecoratorImpl.create(type, config, Optional.of(stream), order);
    }

    @Override
    public Decorator create(String type, Map<String, Object> config, int order) {
        return DecoratorImpl.create(type, config, order);
    }

    @Override
    public Decorator save(Decorator decorator) {
        checkArgument(decorator instanceof DecoratorImpl, "Argument must be an instance of DecoratorImpl, not %s", decorator.getClass());
        if (!Strings.isNullOrEmpty(decorator.id())) {
            this.coll.updateById(decorator.id(), (DecoratorImpl)decorator);
            return this.coll.findOneById(decorator.id());
        }
        return this.coll.save((DecoratorImpl) decorator).getSavedObject();
    }

    @Override
    public int delete(String id) {
        return this.coll.removeById(id).getN();
    }

    private List<Decorator> toInterfaceList(List<DecoratorImpl> concreteList) {
        return concreteList.stream().collect(Collectors.toList());
    }
}
