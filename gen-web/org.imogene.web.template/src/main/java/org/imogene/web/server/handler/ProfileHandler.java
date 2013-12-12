package org.imogene.web.server.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.imogene.lib.common.constants.CommonConstants;
import org.imogene.lib.common.constants.CriteriaConstants;
import org.imogene.lib.common.criteria.BasicCriteria;
import org.imogene.lib.common.criteria.ImogConjunction;
import org.imogene.lib.common.criteria.ImogJunction;
import org.imogene.lib.common.dao.ImogActorDao;
import org.imogene.lib.common.entity.ImogActor;
import org.imogene.lib.common.profile.EntityProfile;
import org.imogene.lib.common.profile.EntityProfileDao;
import org.imogene.lib.common.profile.FieldGroupProfile;
import org.imogene.lib.common.profile.FieldGroupProfileDao;
import org.imogene.lib.common.profile.Profile;
import org.imogene.lib.common.profile.ProfileDao;
import org.imogene.web.server.security.ImogBeanFilterHandler;
import org.imogene.web.server.util.HttpSessionUtil;
import org.imogene.web.server.util.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * A data handler for the Profile beans
 * 
 * @author Medes-IMPS
 */
public class ProfileHandler {

	private ProfileDao dao;
	/* MyActorDao for Foreign Key Deletion */
	private ImogActorDao actorProfilesDao;
	/* EntityProfileDao for Foreign Key Deletion */
	private EntityProfileDao entityProfileDao;
	/* FieldGroupProfileDao for Foreign Key Deletion */
	private FieldGroupProfileDao fieldGroupProfileDao;

	private EntityProfileHandler entityProfileHandler;

	private FieldGroupProfileHandler fieldGroupProfileHandler;

	/**
	 * Loads the entity with the specified id
	 * 
	 * @param entityId the entity id
	 * @return the entity or null
	 */
	@Transactional
	public Profile findById(String entityId) {
		return dao.load(entityId);
	}

	/**
	 * Loads the entity with the specified id
	 * 
	 * @param entityId the entity id
	 * @return the entity or null
	 */
	@Transactional(readOnly = true)
	public Profile getById(String entityId) {
		return dao.getById(entityId);
	}

	/**
	 * Saves or updates the entity
	 * 
	 * @param entity the entity to be saved or updated
	 * @param isNew true if it is a new entity added for the first time.
	 */
	@Transactional
	public void save(Profile entity, boolean isNew) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);

		if (entity != null) {

			if (isNew) {
				entity.setCreated(new Date(System.currentTimeMillis()));
				entity.setCreatedBy(actor.getLogin());
			}

			entity.setModified(new Date(System.currentTimeMillis()));
			entity.setModifiedBy(actor.getLogin());
			entity.setModifiedFrom(CommonConstants.IS_WEB);

			dao.saveOrUpdate(entity, isNew);
		}
	}

	/**
	 * Lists the entities of type Profile
	 * 
	 * @param sortProperty the property used to sort the collection
	 * @param sortOrder true for an ascendant sort
	 * @return list of profile
	 */
	@Transactional(readOnly = true)
	public List<Profile> listProfile(String sortProperty, boolean sortOrder) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);
		ImogJunction junction = createFilterJuntion(actor);

		List<Profile> beans = dao.load(sortProperty, sortOrder, junction);

		return beans;
	}

	/**
	 * Lists the entities of type Profile
	 * 
	 * @param i first index to retrieve
	 * @param j nb of items to retrieve
	 * @param sortProperty the property used to sort the collection
	 * @param sortOrder true for an ascendant sort
	 * @return list of profile
	 */
	@Transactional(readOnly = true)
	public List<Profile> listProfile(int i, int j, String sortProperty, boolean sortOrder) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);
		ImogJunction junction = createFilterJuntion(actor);

		List<Profile> beans = dao.load(i, j, sortProperty, sortOrder, junction);

		return beans;
	}

	/**
	 * Lists the entities of type Profile
	 * 
	 * @param i first index to retrieve
	 * @param j nb of items to retrieve
	 * @param sortProperty the property used to sort the collection
	 * @param sortOrder true for an ascendant sort
	 * @param criterions request criteria
	 * @return list of profile
	 */
	@Transactional(readOnly = true)
	public List<Profile> listProfile(int i, int j, String sortProperty, boolean sortOrder, ImogJunction criterions) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);
		ImogJunction junction = createFilterJuntion(actor);
		if (criterions != null)
			junction.add(criterions);

		List<Profile> beans = dao.load(i, j, sortProperty, sortOrder, junction);

		return beans;
	}

	/**
	 * Lists the entities of type Profile
	 * 
	 * @param i first index to retrieve
	 * @param j nb of items to retrieve
	 * @param sortProperty the property used to sort the collection
	 * @param sortOrder true for an ascendant sort
	 * @param criterions request criteria
	 * @return list of profile
	 */
	@Transactional(readOnly = true)
	public List<Profile> listProfile(int i, int j, String sortProperty, boolean sortOrder,
			List<BasicCriteria> criterions) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);
		ImogJunction junction = createFilterJuntion(actor);

		ImogJunction junctionForCrit = new ImogConjunction();
		if (criterions != null) {
			for (BasicCriteria crit : criterions)
				junctionForCrit.add(crit);
		}
		junction.add(junctionForCrit);

		List<Profile> beans = dao.load(i, j, sortProperty, sortOrder, junction);

		return beans;
	}

	/**
	 * Lists the non affected entities of type Profile
	 * 
	 * @param i first index to retrieve
	 * @param j nb of items to retrieve
	 * @param sortProperty the property used to sort the collection
	 * @param sortOrder true for an ascendant sort
	 * @param criterion request criteria
	 * @param property the property which is not affected
	 * @return list of profile
	 */
	@Transactional(readOnly = true)
	public List<Profile> listNonAffectedProfile(int i, int j, String sortProperty, boolean sortOrder,
			ImogJunction criterions, String property) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);
		ImogJunction junction = createFilterJuntion(actor);
		if (criterions != null)
			junction.add(criterions);

		List<Profile> beans = dao.loadNonAffected(i, j, sortProperty, sortOrder, property, junction);

		return beans;
	}

	/**
	 * Lists the non affected entities of type Profile
	 * 
	 * @param i first index to retrieve
	 * @param j nb of items to retrieve
	 * @param sortProperty the property used to sort the collection
	 * @param sortOrder true for an ascendant sort
	 * @param property the property which is not affected
	 * @return list of profile
	 */
	@Transactional(readOnly = true)
	public List<Profile> listNonAffectedProfile(int i, int j, String sortProperty, boolean sortOrder, String property) {
		return listNonAffectedProfile(i, j, sortProperty, sortOrder, null, property);
	}

	/**
	 * Used when Profile is involved in a Relation 1 <-> 1 Association and is the ReverseRelationField of the Relation
	 * Return all instance of Profile non affected regarding specified property.
	 * 
	 * @param i first index to retrieve
	 * @param j nb of items to retrieve
	 * @param sortProperty the property used to sort the collection
	 * @param sortOrder true for an ascendant sort
	 * @param ImogJunction request criteria
	 * @param property the property which is not affected
	 * @return list of profile
	 */
	@Transactional(readOnly = true)
	public List<Profile> listNonAffectedProfileReverse(int i, int j, String sortProperty, boolean sortOrder,
			ImogJunction criterions, String property) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);
		ImogJunction junction = createFilterJuntion(actor);
		if (criterions != null)
			junction.add(criterions);

		List<Profile> beans = dao.loadNonAffectedReverse(i, j, sortProperty, sortOrder, property, junction);

		return beans;
	}

	/**
	 * Used when Profile is involved in a Relation 1 <-> 1 Association and is the ReverseRelationField of the Relation
	 * Return all instance of Profile non affected regarding specified property.
	 * 
	 * @param i first index to retrieve
	 * @param j nb of items to retrieve
	 * @param sortProperty the property used to sort the collection
	 * @param sortOrder true for an ascendant sort
	 * @param property the property which is not affected
	 * @return list of profile
	 */
	@Transactional(readOnly = true)
	public List<Profile> listNonAffectedProfileReverse(int i, int j, String sortProperty, boolean sortOrder,
			String property) {
		return listNonAffectedProfileReverse(i, j, sortProperty, sortOrder, null, property);
	}

	/**
	 * Gets an empty list of Profile
	 * 
	 * @return an empty list of Profile
	 */
	@Transactional(readOnly = true)
	public List<Profile> getProfileEmptyList() {
		return new ArrayList<Profile>();
	}

	/**
	 * Counts the number of profile in the database
	 * 
	 * @return the count
	 */
	@Transactional(readOnly = true)
	public Long countProfile() {
		return countProfile(null);
	}

	/**
	 * Counts the number of profile in the database, that match the criteria
	 * 
	 * @return the count
	 */
	@Transactional(readOnly = true)
	public Long countProfile(ImogJunction criterions) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);
		ImogJunction junction = createFilterJuntion(actor);
		if (criterions != null)
			junction.add(criterions);

		return dao.count(junction);
	}

	/**
	 * Counts the number of non affected profile entities in the database
	 * 
	 * @param property the property which is not affected
	 * @param criterion request criteria
	 * @return the count
	 */
	@Transactional(readOnly = true)
	public Long countNonAffectedProfile(String property, ImogJunction criterions) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);
		ImogJunction junction = createFilterJuntion(actor);
		if (criterions != null)
			junction.add(criterions);

		return dao.countNonAffected(property, junction);
	}

	/**
	 * Counts the number of non affected profile entities in the database
	 * 
	 * @param property the property which is not affected
	 * @return the count
	 */
	@Transactional(readOnly = true)
	public Long countNonAffectedProfile(String property) {
		return countNonAffectedProfile(property, null);
	}

	/**
	 * Counts the number of non affected profile entities in the database
	 * 
	 * @param property the property which is not affected
	 * @param criterion request criteria
	 * @return the count
	 */
	@Transactional(readOnly = true)
	public Long countNonAffectedProfileReverse(String property, ImogJunction criterions) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);
		ImogJunction junction = createFilterJuntion(actor);
		if (criterions != null)
			junction.add(criterions);
		return dao.countNonAffectedReverse(property, junction);
	}

	/**
	 * Counts the number of non affected profile entities in the database
	 * 
	 * @param property the property which is not affected
	 * @return the count
	 */
	@Transactional(readOnly = true)
	public Long countNonAffectedProfileReverse(String property) {
		return countNonAffectedProfileReverse(property, null);
	}

	/**
	 * Deletes a group of entities identified by their IDs
	 * 
	 * @param ids Entities to delete IDs
	 * @return The list of deleted entities IDs
	 */
	@Transactional
	public void delete(Set<Profile> entities) {
		if (entities != null) {
			for (Profile entity : entities)
				delete(entity);
		}
	}

	/**
	 * Removes the specified entity from the database
	 * 
	 * @param entity The entity to be deleted
	 */
	@Transactional
	public void delete(Profile entity) {

		// Delete foreign key reference for field Profiles of entity MyActor

		List<ImogActor> resultForMyActorProfiles = actorProfilesDao.loadAffectedCardNProperty("profiles",
				entity.getId());
		if (resultForMyActorProfiles != null) {
			for (ImogActor foreignEntity : resultForMyActorProfiles) {
				foreignEntity.setModified(new Date());
				foreignEntity.removeFromProfiles(entity);
				actorProfilesDao.saveOrUpdate(foreignEntity, false);
			}
		}

		// Delete foreign key reference for field Profile of entity EntityProfile

		ImogJunction searchCriterionsForEntityProfileProfile = new ImogConjunction();
		BasicCriteria criteriaEntityProfileProfile = new BasicCriteria();
		criteriaEntityProfileProfile.setOperation(CriteriaConstants.RELATIONFIELD_OPERATOR_EQUAL);
		criteriaEntityProfileProfile.setValue(entity.getId());
		criteriaEntityProfileProfile.setField("profile.id");
		searchCriterionsForEntityProfileProfile.add(criteriaEntityProfileProfile);

		List<EntityProfile> resultForEntityProfileProfile = entityProfileDao.load("modified", false,
				searchCriterionsForEntityProfileProfile);
		if (resultForEntityProfileProfile != null) {
			for (EntityProfile foreignEntity : resultForEntityProfileProfile) {
				foreignEntity.setModified(new Date());
				foreignEntity.setProfile(null);
				entityProfileDao.saveOrUpdate(foreignEntity, false);
			}
		}

		// Delete foreign key reference for field Profile of entity FieldGroupProfile

		ImogJunction searchCriterionsForFieldGroupProfileProfile = new ImogConjunction();
		BasicCriteria criteriaFieldGroupProfileProfile = new BasicCriteria();
		criteriaFieldGroupProfileProfile.setOperation(CriteriaConstants.RELATIONFIELD_OPERATOR_EQUAL);
		criteriaFieldGroupProfileProfile.setValue(entity.getId());
		criteriaFieldGroupProfileProfile.setField("profile.id");
		searchCriterionsForFieldGroupProfileProfile.add(criteriaFieldGroupProfileProfile);

		List<FieldGroupProfile> resultForFieldGroupProfileProfile = fieldGroupProfileDao.load("modified", false,
				searchCriterionsForFieldGroupProfileProfile);
		if (resultForFieldGroupProfileProfile != null) {
			for (FieldGroupProfile foreignEntity : resultForFieldGroupProfileProfile) {
				foreignEntity.setModified(new Date());
				foreignEntity.setProfile(null);
				fieldGroupProfileDao.saveOrUpdate(foreignEntity, false);
			}
		}

		dao.delete(entity);
	}

	/**
	 * Lists the entities of type Profile for the CSV export
	 */
	@Transactional(readOnly = true)
	public List<Profile> listForCsv(String sortProperty, boolean sortOrder, String name) {

		ImogActor actor = (ImogActor) HttpSessionUtil.getHttpSession().getAttribute(ServerConstants.SESSION_USER);
		ImogJunction junction = createFilterJuntion(actor);

		if (name != null && !name.isEmpty()) {
			BasicCriteria criteria = new BasicCriteria();
			criteria.setOperation(CriteriaConstants.STRING_OPERATOR_CONTAINS);
			criteria.setField("name");
			criteria.setValue(name);
			junction.add(criteria);
		}

		List<Profile> beans = dao.load(sortProperty, sortOrder, junction);
		List<Profile> securedBeans = ImogBeanFilterHandler.getInstance().getFilter().<Profile> toSecure(beans, actor);
		return securedBeans;
	}

	/**
	 * Creates a junction based on the filter field declarations, for the current actor.
	 * 
	 * @param actor the current actor
	 */
	private ImogJunction createFilterJuntion(ImogActor actor) {
		ImogConjunction filterConjunction = new ImogConjunction();
		return filterConjunction;
	}

	/**
	 * Saves entity of type EntityProfile
	 * 
	 * @param entity the EntityProfile to be saved or updated
	 * @param isNew true if it is a new entity added for the first time.
	 */
	public void saveEntityProfiles(EntityProfile entity, boolean isNew) {

		entityProfileHandler.save(entity, isNew);
	}

	/**
	 * Deletes entity of type EntityProfile
	 * 
	 * @param toDelete the EntityProfile to be deleted
	 */
	public void deleteEntityProfiles(EntityProfile toDelete) {
		entityProfileHandler.delete(toDelete);
	}

	/**
	 * Saves entity of type FieldGroupProfile
	 * 
	 * @param entity the FieldGroupProfile to be saved or updated
	 * @param isNew true if it is a new entity added for the first time.
	 */
	public void saveFieldGroupProfiles(FieldGroupProfile entity, boolean isNew) {

		fieldGroupProfileHandler.save(entity, isNew);
	}

	/**
	 * Deletes entity of type FieldGroupProfile
	 * 
	 * @param toDelete the FieldGroupProfile to be deleted
	 */
	public void deleteFieldGroupProfiles(FieldGroupProfile toDelete) {
		fieldGroupProfileHandler.delete(toDelete);
	}

	/**
	 * Setter for bean injection
	 * 
	 * @param dao the Profile Dao
	 */
	public void setDao(ProfileDao dao) {
		this.dao = dao;
	}

	/**
	 * Setter for bean injection
	 * 
	 * @param myActorProfilesDao the MyActor Dao
	 */
	public void setMyActorProfilesDao(ImogActorDao myActorProfilesDao) {
		this.actorProfilesDao = myActorProfilesDao;
	}

	/**
	 * Setter for bean injection
	 * 
	 * @param entityProfileProfileDao the EntityProfile Dao
	 */
	public void setEntityProfileDao(EntityProfileDao entityProfileDao) {
		this.entityProfileDao = entityProfileDao;
	}

	/**
	 * Setter for bean injection
	 * 
	 * @param fieldGroupProfileProfileDao the FieldGroupProfile Dao
	 */
	public void setFieldGroupProfileDao(FieldGroupProfileDao fieldGroupProfileDao) {
		this.fieldGroupProfileDao = fieldGroupProfileDao;
	}

	/**
	 * Setter for bean injection
	 * 
	 * @param entityProfilesHandler the EntityProfile Handler
	 */
	public void setEntityProfileHandler(EntityProfileHandler entityProfileHandler) {
		this.entityProfileHandler = entityProfileHandler;
	}

	/**
	 * Setter for bean injection
	 * 
	 * @param fieldGroupProfilesHandler the FieldGroupProfile Handler
	 */
	public void setFieldGroupProfileHandler(FieldGroupProfileHandler fieldGroupProfileHandler) {
		this.fieldGroupProfileHandler = fieldGroupProfileHandler;
	}
}
