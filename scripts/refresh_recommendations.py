import mysql.connector
import pandas as pd
from itertools import combinations
from collections import Counter

def refresh_weighted_recommendations():
    db_config = {
        'host': 'localhost',
        'user': 'ayush',
        'password': 'ayush',
        'database': 'Test'
    }
    

    WEIGHTS = {'purchase': 10, 'click': 3, 'view': 1}

    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()


        query = "SELECT user_id, product_id, interaction_type FROM user_interaction"
        df = pd.read_sql(query, conn)

        if df.empty:
            print("No interaction data found.")
            return


        df['score'] = df['interaction_type'].map(WEIGHTS)


        user_groups = df.groupby('user_id')
        pair_scores = Counter()

        for user_id, group in user_groups:

            user_items = group.groupby('product_id')['score'].max().to_dict()
            product_ids = list(user_items.keys())

            if len(product_ids) > 1:

                for p1, p2 in combinations(sorted(product_ids), 2):

                    strength_contribution = user_items[p1] + user_items[p2]
                    pair_scores[(p1, p2)] += strength_contribution


        insert_data = []
        for (p1, p2), total_strength in pair_scores.items():
            if total_strength > 5:

                insert_data.append((p1, p2, total_strength))
                insert_data.append((p2, p1, total_strength))


        if insert_data:
            print(f"Refreshing {len(insert_data)} weighted associations...")
            cursor.execute("TRUNCATE TABLE product_recommendations")
            
            insert_query = """
                INSERT INTO product_recommendations (product_id, recommended_product_id, association_strength)
                VALUES (%s, %s, %s)
            """
            

            for i in range(0, len(insert_data), 1000):
                cursor.executemany(insert_query, insert_data[i:i + 1000])
            
            conn.commit()
            print(" Weighted daily refresh successful!")
        else:
            print("No strong associations found to insert.")

    except Exception as e:
        print(f" Error: {e}")
    finally:
        if 'conn' in locals() and conn.is_connected():
            cursor.close()
            conn.close()

if __name__ == "__main__":
    refresh_weighted_recommendations()


# cron job
# 0 3 * * * /Users/ayushsingh/ayush/ecommerce/scripts/venv/bin/python3 /Users/ayushsingh/ayushecommerce/scripts/refresh_recommendations.py >> /Users/ayushsingh/ayushecommerce/scripts/cron_log.txt 2>&1