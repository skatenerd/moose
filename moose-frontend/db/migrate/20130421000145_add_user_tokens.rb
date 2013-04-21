class AddUserTokens < ActiveRecord::Migration
  def change
    create_table :user_tokens do |t|
      t.integer :user_id
      t.integer :token_id

      t.timestamps
    end
  end
end
