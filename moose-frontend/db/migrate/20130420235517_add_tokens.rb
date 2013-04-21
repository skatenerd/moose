class AddTokens < ActiveRecord::Migration
  def change
    create_table :tokens do |t|
      t.string :name

      t.timestamps
    end
  end
end
