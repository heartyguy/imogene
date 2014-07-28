package org.imogene.android.widget.field.edit;

import java.util.ArrayList;

import org.imogene.android.Constants.Extras;
import org.imogene.android.template.R;
import org.imogene.android.util.IntentUtils;
import org.imogene.android.widget.field.ConstraintBuilder;
import org.imogene.android.widget.field.FieldManager;
import org.imogene.android.widget.field.FieldManager.OnActivityResultListener;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import fr.medes.android.database.sqlite.stmt.Where;
import fr.medes.android.util.Arrays;

public abstract class RelationFieldEdit<T> extends BaseFieldEdit<T> implements OnActivityResultListener {

	/**
	 * Interface to allow passing values when creating a new form from a related one. The extras contains the name of
	 * the target field and the value that must be given to the field.
	 */
	public static interface ExtraBuilder {

		/**
		 * Method called when a new form is about to be created and the values to pass as extras argument must be set.
		 * 
		 * @param bundle The bundle in which we can add extras values.
		 */
		public void onCreateExtra(Bundle bundle);
	}

	private ArrayList<ConstraintEntry> mConstraintsBuilders;
	private ArrayList<CommonFieldEntry> mCommonFields;
	private ArrayList<ExtraBuilder> mBuilders;

	protected boolean mHasReverse = false;
	protected int mDisplayRes = R.string.imog__numberOfEntities;
	protected int mOppositeCardinality = -1;
	protected int mType; // 0 for main relation field; 1 for reverse relation
							// field
	protected String mOppositeRelationField;
	protected String mFieldName;
	protected String mTableName;

	protected int mRequestCode;
	protected Uri mContentUri;

	public RelationFieldEdit(Context context) {
		super(context, R.layout.imog__field_relation);
	}

	public RelationFieldEdit(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.imog__field_relation);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RelationField, 0, 0);
		mHasReverse = a.getBoolean(R.styleable.RelationField_hasReverse, false);
		mDisplayRes = a.getResourceId(R.styleable.RelationField_display, R.string.imog__numberOfEntities);
		mOppositeCardinality = a.getInt(R.styleable.RelationField_oppositeCardinality, 0);
		mType = a.getInt(R.styleable.RelationField_relationType, 0);
		a.recycle();
		setOnClickListener(this);
	}

	public void setDisplay(int display) {
		mDisplayRes = display;
	}

	public void setOppositeRelationField(String oppositerelationField) {
		mOppositeRelationField = oppositerelationField;
	}

	public void setFieldName(String fieldName) {
		mFieldName = fieldName;
	}

	public void setTableName(String tableName) {
		mTableName = tableName;
	}

	@Override
	public void onAttachedToHierarchy(FieldManager manager) {
		super.onAttachedToHierarchy(manager);
		manager.registerOnActivityResultListener(this);
		mRequestCode = manager.getNextId();
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		setOnClickListener(readOnly ? null : this);
		setOnLongClickListener(readOnly ? null : this);
	}

	/**
	 * Set the content URI of the related entity.
	 * 
	 * @param contentUri The content URI of the related entity.
	 */
	public void setContentUri(Uri contentUri) {
		mContentUri = contentUri;
	}

	/**
	 * Set the drawable color chip representing the related entity.
	 * 
	 * @param drawable The drawable color chip.
	 */
	public void setDrawable(Drawable drawable) {
		final View color = findViewById(R.id.imog__color);
		if (color != null) {
			color.setBackgroundDrawable(drawable);
		}
	}

	/**
	 * Convenient method to add a common field. The value of the first field will be given to the field of the related
	 * entity which column name is given as second argument.
	 * 
	 * @param commonField The field which value will be passed to the new form.
	 * @param commonName The column name of the related entity to be set.
	 */
	public void registerCommonField(RelationFieldEdit<?> commonField, String commonName) {
		if (mCommonFields == null) {
			mCommonFields = new ArrayList<CommonFieldEntry>();
		}

		mCommonFields.add(new CommonFieldEntry(commonField, commonName));
	}

	/**
	 * Convenient method to add an {@link ExtraBuilder}. The extras values will be passed to the related entity when
	 * created.
	 * 
	 * @param builder The extra builder to add.
	 */
	public void registerExtraBuilder(ExtraBuilder builder) {
		if (mBuilders == null) {
			mBuilders = new ArrayList<ExtraBuilder>();
		}

		mBuilders.add(builder);
	}

	/**
	 * Convenient method to add a hierarchical filter. The constraint builder will build a where clause to filter data
	 * upon the given column.
	 * 
	 * @param builder The constraint builder to build the where clause.
	 * @param column The column to filter upon.
	 */
	public void registerConstraintBuilder(ConstraintBuilder builder, String column) {
		if (mConstraintsBuilders == null) {
			mConstraintsBuilders = new ArrayList<ConstraintEntry>();
		}

		mConstraintsBuilders.add(new ConstraintEntry(builder, column));

		builder.registerConstraintDependent(this);
	}

	@Override
	protected void dispatchClick(View v) {
		final Intent intent = new Intent(Intent.ACTION_PICK, mContentUri);
		Where where = new Where();
		boolean and = false;
		Where preparedWhere = onPrepareWhere();
		if (preparedWhere != null) {
			where.clause(preparedWhere);
			and = true;
		}
		onPrepareIntent(intent);
		if (mConstraintsBuilders != null) {
			for (ConstraintEntry entry : mConstraintsBuilders) {
				Where constraintWhere = entry.first.onCreateConstraint(entry.second);
				if (constraintWhere != null) {
					if (and) {
						where.and();
					} else {
						and = true;
					}
					where.clause(constraintWhere);
				}
			}
		}
		if (and) {
			IntentUtils.putWhereExtras(intent, where);
		}
		intent.putExtra(Extras.EXTRA_ENTITY, createBundle());
		getFieldManager().getActivity().startActivityForResult(intent, mRequestCode);
	}

	protected Bundle createBundle() {
		Bundle bundle = new Bundle();
		if (mHasReverse && mOppositeCardinality == 1) {
			bundle.putParcelable(mOppositeRelationField, getFieldManager().getUri());
		}
		if (mCommonFields != null && !mCommonFields.isEmpty()) {
			for (CommonFieldEntry entry : mCommonFields) {
				if (entry.first instanceof RelationOneFieldEdit) {
					bundle.putParcelable(entry.second, ((RelationOneFieldEdit) entry.first).getValue());
				} else if (entry.first instanceof RelationManyFieldEdit) {
					bundle.putParcelableArrayList(entry.second,
							Arrays.asArrayList(((RelationManyFieldEdit) entry.first).getValue()));
				}
			}
		}
		if (mBuilders != null) {
			for (ExtraBuilder builder : mBuilders) {
				builder.onCreateExtra(bundle);
			}
		}
		return bundle;
	}

	protected void onPrepareIntent(Intent intent) {

	}

	protected Where onPrepareWhere() {
		return null;
	}

	/**
	 * Container to ease passing around a tuple of constraint builder and a column to filter upon on which the where
	 * clause will apply.
	 */
	private static final class ConstraintEntry extends Pair<ConstraintBuilder, String> {

		public ConstraintEntry(ConstraintBuilder first, String second) {
			super(first, second);
		}

	};

	/**
	 * Container to ease passing around a tuple of the field which value will be passed to the related form and the name
	 * of the field that will receive the value.
	 */
	private static class CommonFieldEntry extends Pair<RelationFieldEdit<?>, String> {

		public CommonFieldEntry(RelationFieldEdit<?> first, String second) {
			super(first, second);
		}

	}
}
